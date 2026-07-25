import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomeView from '@/views/HomeView.vue'
import ProjectsView from '@/views/ProjectsView.vue'
import TimeEntryView from '@/views/TimeEntryView.vue'
import LoginView from '@/views/LoginView.vue'
import AdminUsersView from '@/views/admin/AdminUsersView.vue'
import ProfileView from '@/views/ProfileView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/projects',
    name: 'projects',
    component: ProjectsView,
    meta: { requiresAuth: true }
  },
  {
    path: '/time-entries',
    name: 'time-entries',
    component: TimeEntryView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: AdminUsersView,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'nav-link-active',
  linkExactActiveClass: 'nav-link-exact'
})
router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  
  // Wenn wir ein Token haben, aber noch keinen User, User-Daten nachladen
  if (authStore.token && !authStore.user) {
    await authStore.fetchCurrentUser()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return {
      name: 'login',
      query: { redirect: to.fullPath } // Weiterleitung nach dem Login
    }
  }
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return { name: 'home' } // Weiterleitung zur Startseite, wenn kein Admin
  }

})

export default router
