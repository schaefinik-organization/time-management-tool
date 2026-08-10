import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/authStore'
import {useUserStore} from '@/stores/userStore'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import TimeTracker from '@/views/TimeTrackerView.vue'
import ProjectReport from '@/views/ProjectReportView.vue'
import AdminUsers from '@/views/admin/AdminUsersView.vue'
import ManagerUsers from '@/views/manager/ManagerUsersView.vue'
import ProjectDashboard from '@/views/ProjectDashboard.vue'

const routes = [

    {
        path: '/login',
        name: 'login',
        component: LoginView
    },

    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        name: 'tracker',
        path: '/tracker',
        component: TimeTracker,
        meta: {
            requiresAuth: true,
            allowedRoles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN']
        }
    },
     {
        name: 'projects',
        path: '/projects',
        component: ProjectDashboard,
        meta: {
            requiresAuth: true,
            allowedRoles: ['ROLE_MANAGER', 'ROLE_ADMIN']
        }
    },
    {
        name: 'reports',
        path: '/reports/:id?',
        component: ProjectReport,
        meta: {
            requiresAuth: true,
            allowedRoles: ['ROLE_MANAGER', 'ROLE_ADMIN']
        }
    },
      {
        name: 'manager-users',
        path: '/manager/users',
        component: ManagerUsers,
        meta: {
            requiresAuth: true,
            allowedRoles: ['ROLE_MANAGER', 'ROLE_ADMIN']
        }
    },
    {
        name: 'admin-users',
        path: '/admin/users',
        component: AdminUsers,
        meta: {
            requiresAuth: true,
            allowedRoles: ['ROLE_ADMIN']
        }
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()
    console.log("isAuthenticated in authStore:" + authStore.isAuthenticated);

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        return next('/login')
    }

    const userStore = useUserStore()
    const userRole = userStore.currentUser?.role;
    if (to.meta.allowedRoles && userRole) {
        const hasRole = to.meta.allowedRoles.includes(userRole)
        if (!hasRole) {
            return next('/login')
        }
    }
    next()
})

export default router