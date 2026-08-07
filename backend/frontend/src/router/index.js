import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/authStore'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import TimeTracker from '@/views/TimeTrackerView.vue'
import ProjectReport from '@/views/ProjectReportView.vue'
import AdminUsers from '@/views/admin/AdminUsersView.vue'

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
        name: 'reports',
        path: '/reports',
        component: ProjectReport,
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
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    console.log(authStore.user);

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        return next('/login')
    }

    if (to.meta.allowedRoles && authStore.user) {
        const hasRole = to.meta.allowedRoles.includes(authStore.user.role)
        if (!hasRole) {
            return next('/login')
        }
    }

    next()
})

export default router