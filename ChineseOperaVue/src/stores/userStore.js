// src/stores/userStore.js
import { defineStore } from 'pinia'
import { login, logout } from '@/api/login'
import { getInfo } from '@/api/user.js'
import { getToken, setToken, removeToken } from '@/utils/auth'

// pinia中的数据 在页面刷新的时候会丢失，状态会被重置，所以需要被持久化在本地。
export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null, // 用户ID
    userName: '', // 用户名
    nickName: '',
    token: getToken(),
    avatar: '',
    roles: '',
    permissions: []
  }),
  actions: {
    login(userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      localStorage.removeItem('user')
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid)
          .then((res) => {
            console.log('登录接口返回数据结构:', res)

            // 基于响应拦截器，res 就是 response.data
            // 兼容不同的数据结构
            let token

            if (res.data?.token) {
              // 结构: { data: { token: "xxx" } }
              token = res.data.token
            } else if (res.token) {
              // 结构: { token: "xxx" }
              token = res.token
            } else {
              console.error('未找到 token 字段:', res)
              reject(new Error('登录响应中未找到 token'))
              return
            }

            setToken(token)
            this.token = token
            resolve()
          })
          .catch((error) => {
            reject(error)
          })
      })
    },
    logOut() {
      return new Promise((resolve, reject) => {
        logout(this.token)
          .then(() => {
            this.token = ''
            this.roles = []
            this.permissions = []
            removeToken()
            resolve()
          })
          .catch((error) => {
            reject(error)
          })
      })
    },
    // 获取用户的详细信息。
    getUserInfo() {
      return new Promise((resolve, reject) => {
        getInfo()
          .then((res) => {
            console.log('获取用户信息接口返回数据结构:', res)

            // 基于响应拦截器返回 response.data，所以这里的 res 就是原始的 response.data
            // 正确的访问路径应该是：
            // - res.data.user (如果后端返回结构是 { data: { user: {...}, userPermissions: [...] } })
            // - 或者 res.user (如果后端直接返回 { user: {...}, userPermissions: [...] })

            let userData, permissions

            if (res.data?.user) {
              // 结构: { data: { user: {...}, userPermissions: [...] } }
              userData = res.data.user
              permissions = res.data.userPermissions
            } else if (res.user) {
              // 结构: { user: {...}, userPermissions: [...] }
              userData = res.user
              permissions = res.userPermissions
            } else {
              console.error('未识别的数据结构，预期包含 user 字段:', res)
              reject(new Error('数据结构不匹配'))
              return
            }

            this.userId = userData.id
            this.userName = userData.userName
            this.nickName = userData.nickName
            this.avatar = userData.avatar
            this.roles = userData.type === 0 ? 'super_admin' : 'admin'
            this.permissions = permissions || []

            resolve(res)
          })
          .catch((error) => {
            reject(error)
          })
      })
    }
  },
  persist: {
    enabled: true, // 启用持久化
    strategies: localStorage
  }
})
export default useUserStore
