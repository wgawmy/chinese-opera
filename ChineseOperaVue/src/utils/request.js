import axios from 'axios'
import { getToken } from '@/utils/auth'

// 封装axios请求
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8081',
  timeout: 10000 // 添加请求超时
})

// 1.请求拦截器
service.interceptors.request.use((config) => {
  const isToken = (config.headers || {}).isToken === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = getToken()
  }
  return config
})

// 2.响应拦截器
service.interceptors.response.use(
  (response) => {
    // 对响应数据做处理
    const res = response.data

    // 添加调试日志
    console.log('API响应数据:', {
      url: response.config.url,
      status: response.status,
      data: res
    })

    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    if (response.status === 200) {
      return res // 注意：这里返回的是 response.data，不是完整的 response
    } else {
      // 处理业务错误
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  (error) => {
    // 对响应错误做处理
    console.error('请求错误:', error)

    // 根据不同的错误状态码进行不同的处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          console.error('未授权，请重新登录')
          break
        case 403:
          console.error('权限不足')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error('请求失败')
      }
    } else {
      console.error('网络错误')
    }

    return Promise.reject(error)
  }
)

export default service
