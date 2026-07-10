<template>
  <div class="app-shell">
    <header class="app-header">
      <div>
        <h1>Time Management Tool</h1>
        <p>Projektzeiten erfassen und pro Tag anzeigen</p>
      </div>

      <nav class="nav-links">
        <RouterLink to="/">Dashboard</RouterLink>
        <RouterLink to="/projects">Projekte</RouterLink>
      </nav>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
</script>
