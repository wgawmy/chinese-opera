// 应用入口：创建 Vue 应用并挂载全局插件、路由和状态管理
import './assets/styles/index.scss'
import directive from './diretive'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElIconModules from '@element-plus/icons-vue'
import { initializeUserState } from '@/utils/authSync'


// 在开发环境显示环境信息
if (import.meta.env.DEV) {
  console.log('当前环境信息:')
  console.log('Mode:', import.meta.env.MODE)
  console.log('API Base URL:', import.meta.env.VITE_API_BASE_URL)
  console.log('App Title:', import.meta.env.VITE_APP_TITLE)
  console.log('App Env:', import.meta.env.VITE_APP_ENV)
  console.log('Is Dev:', import.meta.env.DEV)
  console.log('Is Prod:', import.meta.env.PROD)
}

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
const app = createApp(App)

// 统一注册Icon图标
for (const iconName in ElIconModules) {
  if (Reflect.has(ElIconModules, iconName)) {
    const item = ElIconModules[iconName]
    app.component(iconName, item)
  }
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)
directive(app)

// 应用启动时验证token有效性
const initializeApp = async () => {
  try {
    console.log('🔄 初始化用户登录状态...')
    await initializeUserState()
    console.log('✅ 用户状态初始化完成')
  } catch (error) {
    console.warn('⚠️ 用户状态初始化失败，但应用将继续启动:', error)
  } finally {
    app.mount('#app')
    console.log('🎉 应用启动完成')
  }
}

// 启动应用
initializeApp()
