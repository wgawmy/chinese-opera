// 环境变量工具
export const env = {
  // API 基础URL
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8081',

  // 应用标题
  APP_TITLE: import.meta.env.VITE_APP_TITLE || '中国戏曲应用',

  // 当前环境
  APP_ENV: import.meta.env.VITE_APP_ENV || 'development',

  // 是否为开发环境
  isDev: import.meta.env.DEV,

  // 是否为生产环境
  isProd: import.meta.env.PROD,

  // 运行模式
  MODE: import.meta.env.MODE
}

// 打印当前环境信息（仅在开发环境）
if (env.isDev) {
  console.log('当前环境配置:', {
    API_BASE_URL: env.API_BASE_URL,
    APP_TITLE: env.APP_TITLE,
    APP_ENV: env.APP_ENV,
    MODE: env.MODE
  })
}
