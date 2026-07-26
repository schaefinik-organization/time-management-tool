<template>
  <section class="space-y-6">
    <!-- Header -->
    <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <div>
        <h1 class="text-2xl font-semibold text-gray-900">Benutzerverwaltung</h1>
        <p class="text-sm text-gray-500">Erstelle neue Accounts oder verwalte bestehende Berechtigungen.</p>
      </div>
      <BaseButton @click="openCreateModal">
        + Neuer Benutzer
      </BaseButton>
    </div>

    <!-- Filter & Suche -->
    <BaseCard>
      <div class="flex flex-col gap-4 md:flex-row md:items-center">
        <div class="flex-1">
          <BaseInput v-model="filter" placeholder="Suche nach Name, E-Mail oder Rolle..." />
        </div>
        <BaseButton variant="secondary" @click="fetchUsers" :disabled="loading">
          Aktualisieren
        </BaseButton>
      </div>
    </BaseCard>

    <!-- Error Anzeige -->
    <AppError :message="globalError" />

    <!-- User Tabelle -->
    <BaseCard class="overflow-hidden p-0">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 border-b font-medium text-gray-700">
            <tr>
              <th class="px-6 py-4">Benutzername</th>
              <th class="px-6 py-4">E-Mail</th>
              <th class="px-6 py-4">Rolle</th>
              <th class="px-6 py-4">Status</th>
              <th class="px-6 py-4 text-right">Aktionen</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 font-medium text-gray-900">{{ user.username }}</td>
              <td class="px-6 py-4 text-gray-600">{{ user.email }}</td>
              <td class="px-6 py-4">
                <span class="rounded-full px-2 py-1 text-xs font-semibold" 
                  :class="user.role === 'ROLE_ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'">
                  {{ user.role }}
                </span>
              </td>
              <td class="px-6 py-4">
                <span :class="user.enabled ? 'text-green-600' : 'text-red-600'" class="flex items-center gap-1">
                  <span class="h-2 w-2 rounded-full" :class="user.enabled ? 'bg-green-600' : 'bg-red-600'"></span>
                  {{ user.enabled ? 'Aktiv' : 'Inaktiv' }}
                </span>
              </td>
              <td class="px-6 py-4 text-right space-x-2">
                <button @click="openEditModal(user)" class="text-blue-600 hover:underline">Bearbeiten</button>
                <button @click="confirmDelete(user)" class="text-red-600 hover:underline">Löschen</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!loading && !filteredUsers.length" class="p-8 text-center text-gray-500">
        Keine Benutzer gefunden.
      </div>
    </BaseCard>

    <!-- Modal für Erstellen / Bearbeiten -->
    <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 p-4">
      <BaseCard class="w-full max-w-lg shadow-2xl">
        <h2 class="mb-4 text-xl font-bold">{{ isEditMode ? 'Benutzer bearbeiten' : 'Neuen Benutzer anlegen' }}</h2>
        
        <form @submit.prevent="saveUser" class="space-y-4">
          <div class="grid gap-4 sm:grid-cols-2">
            <label class="text-sm font-medium">Benutzername</label>
            <BaseInput v-model="form.username" label="Benutzername" required :disabled="isEditMode" />
            <label class="text-sm font-medium">E-Mail</label>
            <BaseInput v-model="form.email" label="E-Mail" type="email" required />
          </div>

          <div v-if="!isEditMode" class="space-y-2 text-sm">
            <BaseInput v-model="form.password" label="Passwort" type="password" required />
            <p class="text-gray-500 text-xs italic">Initiales Passwort für den Benutzer.</p>
          </div>

          <div class="grid gap-4 sm:grid-cols-2">
            <div class="space-y-1">
              <label class="text-sm font-medium">Rolle</label>
              <BaseSelect v-model="form.role">
                <option value="ROLE_USER">Benutzer</option>
                <option value="ROLE_ADMIN">Administrator</option>
              </BaseSelect>
            </div>
            <div class="flex items-end pb-2">
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" v-model="form.enabled" class="h-4 w-4 rounded border-gray-300" />
                <span class="text-sm font-medium">Account aktiviert</span>
              </label>
            </div>
          </div>

          <AppError :message="modalError" :errors="validationErrors" />

          <div class="flex justify-end gap-3 pt-4">
            <BaseButton variant="secondary" type="button" @click="showModal = false">Abbrechen</BaseButton>
            <BaseButton type="submit" :disabled="modalLoading">
              {{ modalLoading ? 'Speichert...' : 'Speichern' }}
            </BaseButton>
          </div>
        </form>
      </BaseCard>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { useFormHandler } from '@/composables/useFormHandler';
import { fetchUsersApi, createUserApi, updateUserApi, deleteUserApi } from '@/api/admin/users';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';
import BaseInput from '@/components/ui/BaseInput.vue';
import BaseSelect from '@/components/ui/BaseSelect.vue';
import AppError from '@/components/ui/AppError.vue';

const users = ref([]);
const filter = ref('');
const loading = ref(false);
const globalError = ref('');
const showModal = ref(false);
const isEditMode = ref(false);
const editingUserId = ref(null);

const { 
  loading: modalLoading, 
  errorMessage: modalError, 
  validationErrors, 
  handleAction 
} = useFormHandler();

const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'ROLE_USER',
  enabled: true
});

const fetchUsers = async () => {
  loading.value = true;
  globalError.value = '';
  try {
    const response = await fetchUsersApi();
    users.value = response.data;
  } catch (err) {
    globalError.value = 'Benutzer konnten nicht geladen werden.';
  } finally {
    loading.value = false;
  }
};

const openCreateModal = () => {
  isEditMode.value = false;
  editingUserId.value = null;
  Object.assign(form, { username: '', email: '', password: '', role: 'ROLE_USER', enabled: true });
  showModal.value = true;
};

const openEditModal = (user) => {
  isEditMode.value = true;
  editingUserId.value = user.id;
  Object.assign(form, { ...user, password: '' });
  showModal.value = true;
};

const saveUser = async () => {
  const action = isEditMode.value 
    ? () => updateUserApi(editingUserId.value, form)
    : () => createUserApi(form);

  await handleAction(action, () => {
    showModal.value = false;
    fetchUsers();
  });
};

const confirmDelete = async (user) => {
  if (confirm(`Soll der Benutzer "${user.username}" wirklich dauerhaft gelöscht werden?`)) {
    try {
      await deleteUserApi(user.id);
      fetchUsers();
    } catch (err) {
      globalError.value = 'Löschen fehlgeschlagen.';
    }
  }
};

const filteredUsers = computed(() => {
  const s = filter.value.toLowerCase();
  return users.value.filter(u => 
    u.username.toLowerCase().includes(s) || 
    u.email.toLowerCase().includes(s) || 
    u.role.toLowerCase().includes(s)
  );
});

onMounted(fetchUsers);
</script>