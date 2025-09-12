/**
 * Token同步和状态管理工具
 * 解决前后端登录状态不同步问题
 */

import useUserStore from '@/stores/userStore'
import { ElMessage } from 'element-plus'

/**
 * 检查并同步用户登录状态
 * @returns {Promise<boolean>} 是否为有效登录状态
 */
export async function syncUserLoginState() {
  const userStore = useUserStore()

  // 如果没有token，直接返回false
  if (!userStore.token) {
    console.log('No token found, user not logged in')
    return false
  }

  try {
    // 验证token有效性
    console.log('Validating token...')
    const isValid = await userStore.validateToken()

    if (isValid) {
      console.log('Token is valid, user is logged in')
      return true
    } else {
      console.warn('Token is invalid, clearing user state')
      return false
    }
  } catch (error) {
    console.error('Failed to validate token:', error)
    userStore.clearUserInfo()
    return false
  }
}

/**
 * 安全退出登录
 * 确保前后端状态同步
 */
export async function safeLogout() {
  const userStore = useUserStore()

  try {
    // 尝试调用后端退出接口
    await userStore.logOut()
    console.log('Logout successful')
    return true
  } catch (error) {
    console.warn('Backend logout failed, but will clear local state:', error)
    // 即使后端退出失败，也要清理本地状态
    userStore.clearUserInfo()
    return false
  }
}

/**
 * 处理token过期
 * 清理状态并跳转登录页
 */
export function handleTokenExpired() {
  const userStore = useUserStore()
  userStore.clearUserInfo()

  ElMessage.warning('登录已过期，请重新登录')

  // 避免在登录页重复跳转
  if (window.location.pathname !== '/login') {
    // 保存当前路径用于登录后重定向
    const currentPath = window.location.pathname + window.location.search
    window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
  }
}

/**
 * 应用启动时的用户状态初始化
 */
export async function initializeUserState() {
  const userStore = useUserStore()

  if (!userStore.token) {
    console.log('No token found during initialization')
    return false
  }

  try {
    console.log('Initializing user state...')
    const isValid = await userStore.validateToken()

    if (isValid) {
      console.log('User state initialized successfully')
      return true
    } else {
      console.log('Token invalid during initialization, cleared state')
      return false
    }
  } catch (error) {
    console.error('Failed to initialize user state:', error)
    userStore.clearUserInfo()
    return false
  }
}

/**
 * 检查用户是否已登录
 * @returns {boolean}
 */
export function isUserLoggedIn() {
  const userStore = useUserStore()
  return !!(userStore.token && userStore.userId)
}

/**
 * 获取当前用户信息
 * @returns {object|null}
 */
export function getCurrentUser() {
  const userStore = useUserStore()

  if (!isUserLoggedIn()) {
    return null
  }

  return {
    userId: userStore.userId,
    userName: userStore.userName,
    nickName: userStore.nickName,
    avatar: userStore.avatar,
    roles: userStore.roles,
    permissions: userStore.permissions
  }
}

/**
 * 强制刷新用户信息
 * 用于同步最新的用户状态
 */
export async function refreshUserInfo() {
  const userStore = useUserStore()

  if (!userStore.token) {
    throw new Error('No token available')
  }

  try {
    await userStore.getUserInfo()
    console.log('User info refreshed successfully')
    return true
  } catch (error) {
    console.error('Failed to refresh user info:', error)
    throw error
  }
}

export default {
  syncUserLoginState,
  safeLogout,
  handleTokenExpired,
  initializeUserState,
  isUserLoggedIn,
  getCurrentUser,
  refreshUserInfo
}
