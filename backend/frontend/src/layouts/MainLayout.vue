<template>
  <div class="app-layout">
    <!-- Sidebar -->
    <timetracker-sidebar />
    <!-- Main Content Area -->
    <div class="main-content">
      <!-- Topbar -->
      <timetracker-topbar />
      
      <!-- Hier wird der Inhalt der jeweiligen View gerendert -->
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import TimetrackerSidebar from '@/layouts/TimetrackerSidebar.vue'
import TimetrackerTopbar from '@/layouts/TimetrackerTopbar.vue'

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