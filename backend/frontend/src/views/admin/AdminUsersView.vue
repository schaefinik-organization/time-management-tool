<template>
  <div class="admin-users-view">
    <header class="admin-users-view__header">
      <div>
        <h1>Admin User Management</h1>
        <p>View and manage registered users in the system.</p>
      </div>
      <div class="admin-users-view__controls">
        <input
          v-model="filter"
          type="search"
          placeholder="Search by name, email, or role"
          aria-label="Search users"
        />
        <button type="button" @click="fetchUsers" :disabled="loading">
          Refresh
        </button>
      </div>
    </header>

    <main class="admin-users-view__main">
      <div v-if="loading" class="status-message">Loading users...</div>
      <div v-else-if="error" class="status-message status-message--error">{{ error }}</div>
      <table v-else class="users-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.role }}</td>
            <td>{{ user.enabled ? 'Active' : 'Inactive' }}</td>
            <td>
              <button
                type="button"
                @click="toggleActive(user)"
                :disabled="busyUsers[user.id]"
              >
                {{ user.enabled ? 'Deactivate' : 'Activate' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!loading && !filteredUsers.length" class="status-message">
        No users found.
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { fetchUsersApi, updateUserApi } from '@/api/admin/users';
const testUsers = ref([
  {"id":1,"username":"admin","email":"admin@example.com","role":"ROLE_ADMIN","enabled":true},
  {"id":2,"username":"dominik","email":"dominik@example.com","role":"ROLE_USER","enabled":true},
  {"id":3,"username":"test","email":"test@example.com","role":"ROLE_USER","enabled":true}
]);
const users = ref([]);
const loading = ref(false);
const error = ref('');
const filter = ref('');
const busyUsers = ref({});

const fetchUsers = async () => {
  loading.value = true;
  error.value = '';
  console.log('Fetching users...');
  try {
    const response = await fetchUsersApi();
    console.log('Raw response:', response);
    if (!response.status || response.status < 200 || response.status >= 300) {
      throw new Error(`Unable to load users (${response.status})`);
    }

    console.log('Response:', response);
    const data = await response.data;
    users.value = Array.isArray(data) ? data : [];
    console.log('Fetched users:', users.value);
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to fetch users';
  } finally {
    loading.value = false;
  }
};

const toggleActive = async (user) => {
  busyUsers.value = { ...busyUsers.value, [user.id]: true };
  error.value = '';

  try {
    const response = await updateUserApi(user.id, { enabled: !user.enabled });

    if (!response.status || response.status < 200 || response.status >= 300) {
      throw new Error(`Unable to update user status (${response.status})`);
    }

    const updatedUser = await response.data;
    users.value = users.value.map((existing) =>
      existing.id === updatedUser.id ? updatedUser : existing
    );
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to update status';
  } finally {
    busyUsers.value = { ...busyUsers.value, [user.id]: false };
  }
};

const filteredUsers = computed(() => {
  const search = filter.value.trim().toLowerCase();
  if (!search) {
    return users.value;
  }

  return users.value.filter((user) => {
    return (
      user.name?.toLowerCase().includes(search) ||
      user.email?.toLowerCase().includes(search) ||
      user.role?.toLowerCase().includes(search)
    );
  });
});

onMounted(fetchUsers);
</script>

<style scoped>
.admin-users-view {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.5rem;
}

.admin-users-view__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.admin-users-view__controls {
  display: flex;
  gap: 0.75rem;
}

.admin-users-view__controls input {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 0.375rem;
  min-width: 220px;
}

.admin-users-view__controls button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.375rem;
  background: #2b6cb0;
  color: #fff;
  cursor: pointer;
}

.admin-users-view__controls button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 0.75rem 0.75rem;
  border: 1px solid #e2e8f0;
  text-align: left;
}

.users-table th {
  background: #edf2f7;
  font-weight: 600;
}

.users-table button {
  padding: 0.35rem 0.75rem;
  border: none;
  border-radius: 0.35rem;
  background: #4a5568;
  color: #fff;
  cursor: pointer;
}

.status-message {
  padding: 1rem;
  color: #4a5568;
}

.status-message--error {
  color: #c53030;
}
</style>
