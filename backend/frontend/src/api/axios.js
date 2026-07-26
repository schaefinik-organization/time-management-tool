import axios from 'axios';
import { useAuthStore } from '@/stores/auth'; // Beispiel für einen Pinia Store
import router from '@/router';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Token an jeden Request hängen
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response Interceptor: Fängt alle Antworten ab
api.interceptors.response.use(
  (response) => response, // Erfolgreiche Antworten einfach durchreichen
  (error) => {
    const { status, data } = error.response;

    switch (status) {
      case 401:
        // Nicht eingeloggt -> zum Login leiten
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

    // WICHTIG: Den Fehler weitergeben, damit die Komponente 
    // auf spezifische Fehler (wie 400 Validation) reagieren kann.
    return Promise.reject(error);
  }
);

export default api;