import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';
import router from '@/router';
import mockData from '@/api/mocks/mocks'

const USE_MOCKS = false;

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    if (USE_MOCKS) {
        const targetUrl = config.url || '';
        const matchingMock = mockData[targetUrl] || mockData[targetUrl.replace(api.defaults.baseURL, '')];

        if (matchingMock) {
            const mockResponse = {
                data: matchingMock,
                status: 200,
                statusText: 'OK',
                headers: {},
                config: config
            };
            
            config.cancelToken = new axios.CancelToken((cancel) => {
                cancel(JSON.stringify(mockResponse));
            });
        }
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

api.interceptors.response.use(
    (response) => response, 
    (error) => {
        if (USE_MOCKS && axios.isCancel(error)) {
            try {
                const mockResponse = JSON.parse(error.message);
                return Promise.resolve(mockResponse); 
            } catch (e) {
            }
        }

        if (error.response) {
            const { status, data } = error.response;

            switch (status) {
                case 401:
                    const authStore = useAuthStore();
                    authStore.logout();
                    router.push('/');
                    break;

                case 403:
                    alert("Keine Berechtigung für diese Aktion.");
                    break;

                case 404:
                    console.error("Ressource nicht gefunden:", data?.message || "404");
                    break;

                case 500:
                    alert("Serverfehler. Bitte versuchen Sie es später erneut.");
                    break;
            }
        }

        return Promise.reject(error);
    }
);

export default api;
