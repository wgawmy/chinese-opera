import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), vueJsx(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 确保这里配置的全局 SCSS 文件路径正确
        additionalData: `@import "@/assets/styles/var.scss"; @import "@/assets/styles/mixin.scss";`
      }
    }
  },
  build: {
    // 启用代码分割
    rollupOptions: {
      output: {
        // 手动代码分割
        manualChunks: {
          // 将第三方库分离
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          utils: ['axios', 'crypto-js', 'js-cookie'],
          audio: ['howler'],
          charts: ['echarts'],
          three: ['three', 'vanta']
        }
      }
    },
    // 压缩选项
    minify: 'terser',
    terserOptions: {
      compress: {
        // 生产环境时移除console
        drop_console: true,
        drop_debugger: true
      }
    },
    // 启用gzip压缩提示
    reportCompressedSize: false
  }
})
