import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/authStore'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'

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
        component: () => import('@/views/TimeTrackerView.vue'),
        meta: {requiresAuth: true, allowedRoles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN']}
    },
    {
        name: 'reports',
        path: '/reports',
        component: () => import('@/views/ProjectReportView.vue'),
        meta: {requiresAuth: true, allowedRoles: ['ROLE_MANAGER', 'ROLE_ADMIN']}
    },
    {
        name: 'admin-users',
        path: '/admin/users',
        component: () => import('@/views/admin/AdminUsersView.vue'),
        meta: {requiresAuth: true, allowedRoles: ['ROLE_ADMIN']}
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        return next('/login')
    }

    if (to.meta.allowedRoles && authStore.user) {
        const hasRole = to.meta.allowedRoles.includes(authStore.user.role)
        if (!hasRole) {
            return next('/tracker')
        }
    }

    next()
})

export default router