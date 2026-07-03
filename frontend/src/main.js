import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { enumLabel } from './utils/enums'
import './styles.css'

const app = createApp(App)

app.config.globalProperties.$enumLabel = enumLabel
app.use(createPinia())
app.use(router)
app.mount('#app')
