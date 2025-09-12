import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'

// 公共路由
const constantRoutes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/operaplay',
    name: 'operaPlay',
    component: () => import('@/views/OperaPlay.vue')
  },
  {
    path: '/hotlist',
    name: 'HotList',
    component: () => import('@/views/HotList.vue')
  },
  {
    path: '/operatag',
    name: 'OperaTagShow',
    component: () => import('@/views/OperaTagShow.vue')
  },
  {
    path: '/audio',
    name: 'Audio',
    component: () => import('@/views/Audio.vue')
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('@/views/History.vue')
  },
  {
    path: '/userinfo',
    name: 'UserInfo',
    component: () => import('@/views/UserInfo.vue')
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('@/views/Shop.vue')
  },
  {
    path: '/shop-detail',
    name: 'ShopDetail',
    component: () => import('@/views/ShopDetail.vue')
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/test.vue')
  }
]

// 动态路由，需要根据权限加载
const dynamicRoutes = [
  {
    path: '/manage',
    name: 'ManageData',
    component: () => import('@/views/ManageData.vue'),
    children: [
      {
        path: '/manageUploadaudiofile',
        name: 'UploadAudioFile',
        component: () => import('@/components/ManageData/UploadFile/UploadAudio.vue'),
        meta: { title: '上传音频', permissions: ['manage:manageUploadaudiofile'] }
      },
      {
        path: '/manageUploadvideofile',
        name: 'UploadVideoFile',
        component: () => import('@/components/ManageData/UploadFile/UploadVideo.vue'),
        meta: { title: '上传视频', permissions: ['manage:manageUploadvideofile'] }
      },
      {
        path: '/managevideo',
        name: 'ManageVideo',
        component: () => import('@/components/ManageData/ManageVideo/ManageVideo.vue'),
        meta: { title: '视频管理', permissions: ['manage:managevideo'] }
      },
      {
        path: '/manageCollection',
        name: 'ManageCollection',
        component: () => import('@/components/ManageData/ManageCollection/ManageCollection.vue'),
        meta: { title: '合集管理', permissions: ['manage:managecollection'] }
      },
      {
        path: '/manageuser',
        name: 'ManageUser',
        component: () => import('@/components/ManageData/ManagePermission/ManageUser.vue'),
        meta: { title: '用户管理', permissions: ['manage:manageuser'] }
      },
      {
        path: '/managerole',
        name: 'ManageRole',
        component: () => import('@/components/ManageData/ManagePermission/ManageRole.vue'),
        meta: { title: '角色管理', permissions: ['manage:managerole'] }
      },
      {
        path: '/managemenu',
        name: 'ManageMenu',
        component: () => import('@/components/ManageData/ManagePermission/ManageMenu.vue'),
        meta: { title: '权限管理', permissions: ['manage:managemenu'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...constantRoutes, ...dynamicRoutes],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 白名单路由，无需权限控制
const whiteList = [
  '/', // 首页
  '/login', // 登录页
  '/operaplay', // 戏曲播放页
  '/hotlist', // 热门列表
  '/operatag', // 戏曲分类
  '/audio', // 音频页面
  '/shop', // 商店
  '/shop-detail' // 商店详情
] // 公共路由，游客可访问

// 需要登录的路由（用户相关功能）
const authRequiredRoutes = ['/history', '/userinfo']

// 路由守卫，检查权限
router.beforeEach((to, from, next) => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const hasToken = user && user.token

    // 1. 如果是白名单路由，直接放行（无需登录）
    if (whiteList.includes(to.path)) {
      next()
      return
    }

    // 2. 如果是需要登录的路由，检查是否已登录
    if (authRequiredRoutes.includes(to.path)) {
      if (!hasToken) {
        // 未登录，跳转到登录页，并记录原始目标路径
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }

    // 3. 如果是管理后台路由，检查权限
    if (to.path.startsWith('/manage')) {
      if (!hasToken) {
        next('/login')
        return
      }

      if (to.meta?.permissions) {
        const hasPermission =
          user.roles === 'super_admin' ||
          user.roles === 'admin' ||
          (user.permissions && user.permissions.includes(to.meta.permissions[0]))

        if (hasPermission) {
          next()
        } else {
          next('/') // 权限不足，跳转到首页
        }
        return
      }
    }

    // 4. 其他路由，正常放行
    next()
  } catch (error) {
    console.error('路由守卫错误:', error)
    // 如果解析用户信息出错，跳转到首页
    next('/')
  }
})

export default router
