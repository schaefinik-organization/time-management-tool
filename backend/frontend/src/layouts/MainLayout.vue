<template>
  <div class="app-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <h2>TimeTracker</h2>
      <nav>
        <router-link to="/dashboard">Dashboard</router-link>
        <router-link to="/projects">Projekte</router-link>
        <router-link to="/reports">Auswertungen</router-link>
        <router-link to="/timetracker">Zeiterfassung</router-link>
      </nav>
    </aside>

    <!-- Main Content Area -->
    <div class="main-content">
      <header class="topbar">
        <span>Willkommen, {{ currentUser.username }}</span>
        <button @click="logout">Logout</button>
      </header>
      
      <!-- Hier wird der Inhalt der jeweiligen View gerendert -->
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const currentUser = reactive({ username: 'Manager' })

const logout = () => {
  console.log('Logout logic here')
}
</script>

<style scoped>
/* Sehr simples CSS-Grid Layout als Basis */
.app-layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 250px;
  background-color: #2c3e50;
  color: white;
  padding: 20px;
}
.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 30px;
}
.sidebar a {
  color: white;
  text-decoration: none;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 60px;
  background-color: #ecf0f1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.page-content {
  padding: 20px;
}
</style>