import { createApp } from 'vue'
import { createPinia } from 'pinia' // WICHTIG
import App from './App.vue'
import router from './router'       // WICHTIG

const app = createApp(App)

// Diese beiden Zeilen MÜSSEN vor app.mount('#app') stehen!
app.use(createPinia())
app.use(router)

app.mount('#app')