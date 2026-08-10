import axios from 'axios';
import {useAuthStore} from '@/stores/authStore';
import router from '@/router';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

api.interceptors.response.use(
    (response) => response, 
    (error) => {
        const {status, data} = error.response;

        switch (status) {
            case 401:
                const authStore = useAuthStore();
                authStore.logout();
                router.push('/login');
                break;

            case 403:
                alert("Keine Berechtigung für diese Aktion.");
                break;

            case 404:
                console.error("Ressource nicht gefunden:", data.message);
                break;

            case 500:
                alert("Serverfehler. Bitte versuchen Sie es später erneut.");
                break;
        }
        return Promise.reject(error);
    }
);

export default api;