<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/userStore' 

const userStore = useUserStore()

const anonymousRouterLinks = [
  { name: 'Home', path: '/' },
  { name: 'Login', path: '/login' }
]

const userRouterLinks = [
    { name: 'Home', path: '/' },
  { name: 'Zeiterfassung', path: '/tracker' },
   { name: 'Report', path: '/user/report' },
]

const managerRouterLinks = [
  { name: 'Team-Benutzer', path: '/manager/users' },
  { name: 'Reports', path: '/manager/report' },
  { name: 'Projektverwaltung', path: '/projects' },
  { name: 'Berichte', path: '/reports' }

]

const adminRouterLinks = [
  { name: 'Admin Benutzerverwaltung', path: '/admin/users' },
  { name: 'Admin Projektverwaltung', path: '/admin/projects' },
]

const sidebarLinks = computed(() => {
  console.log("inSidebarLinks");
    console.log("userStore.currentUser:" + userStore.currentUser );
  console.log("userStore.currentUser?.role:" + userStore.currentUser?.role );
  const role = userStore.currentUser?.role 

  if (role === 'ROLE_ADMIN') {
    return [...userRouterLinks, ...managerRouterLinks, ...adminRouterLinks]
    
  } else if (role === 'ROLE_MANAGER') {
    return [...userRouterLinks, ...managerRouterLinks]
    
  } else if (role === 'ROLE_USER') {
    return [...userRouterLinks]
    
  } else {
    return anonymousRouterLinks
  }
})
</script>

<template>
  <aside class="sidebar">
    <h2>TimeTracker App</h2>
    <nav class="sidebar-nav">
    <RouterLink
      v-for="link in sidebarLinks" 
      :key="link.path" 
      :to="link.path"
    >
      {{ link.name }}
    </RouterLink>
  </nav>
  </aside>
</template>

<style lang="css" scoped>
.sidebar {
  width: 250px;
  background-color: #2c3e50;
  color: white;
  padding: 20px;
}
.sidebar-nav {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-nav a {
  text-decoration: none;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.sidebar-nav a:hover {
  background-color: #f1f1f1;
  color: grey;
}

/* Vue Router vergibt diese Klasse automatisch an den aktiven Link! */
.sidebar-nav a.router-link-active {
  background-color: #3498db;
  color: white;
  font-weight: bold;
}
</style>