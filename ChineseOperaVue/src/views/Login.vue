<template>
  <div class="login-page">
    <!-- 简化的背景 -->
    <div class="background-container">
      <!-- 轻量级CSS动画背景 -->
      <div class="animated-bg">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
        </div>
      </div>
    </div>

    <!-- 登录内容 -->
    <div class="lr-container">
      <div class="content_body">
        <!-- 加载状态 -->
        <div v-if="pageLoading" class="page-loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 主要内容 -->
        <div v-else>
          <div class="content_header">
            <a :class="isActive ? ['isActive'] : []" @click="lr_change">登录</a>
            <a :class="isActive ? [] : ['isActive']" @click="lr_change">注册</a>
          </div>

          <!-- 登录 -->
          <div class="login_content" v-if="isActive">
            <el-form :model="loginForm" ref="loginFormRef">
              <div class="uname">
                <input
                  type="text"
                  class="ipt"
                  placeholder="账号"
                  v-model="loginForm.username"
                  @keyup.enter="login"
                />
              </div>
              <div class="pwd">
                <input
                  type="password"
                  class="ipt"
                  placeholder="密码"
                  v-model="loginForm.password"
                  @keyup.enter="login"
                />
              </div>

              <div class="identify-code">
                <input
                  type="text"
                  class="ipt code-input"
                  placeholder="请输入验证码"
                  v-model="loginForm.code"
                  @keyup.enter="login"
                />
                <div class="login-code">
                  <img
                    v-if="codeUrl"
                    :src="codeUrl"
                    @click="getCode"
                    class="login-code-img"
                    loading="lazy"
                  />
                  <div v-else class="code-loading">加载中...</div>
                </div>
              </div>

              <div class="rpwd">
                <input type="checkbox" v-model="loginForm.rememberMe" />
                <span>记住密码</span>
              </div>
            </el-form>

            <button class="login_btn" @click="login" :disabled="loginLoading">
              {{ loginLoading ? '登录中...' : '登录' }}
            </button>
          </div>

          <!-- 注册 -->
          <div class="register_content" v-else>
            <form>
              <div class="uname">
                <input
                  type="text"
                  placeholder="请输入需要注册的账号"
                  class="ipt"
                  v-model="registerForm.username"
                />
              </div>
              <div class="psw">
                <input
                  type="password"
                  placeholder="请输入密码"
                  class="ipt"
                  v-model="registerForm.password"
                />
              </div>
            </form>

            <button class="register_btn" @click="register" :disabled="registerLoading">
              {{ registerLoading ? '注册中...' : '注册' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCodeImg, register } from '@/api/login'
import Cookies from 'js-cookie'
import { EncryptedPassword, DecryptPassword } from '@/utils/jsencrypt.js'
import useUserStore from '@/stores/userStore'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    // 响应式数据
    const isActive = ref(true)
    const pageLoading = ref(true)
    const loginLoading = ref(false)
    const registerLoading = ref(false)
    const codeUrl = ref('')

    const loginForm = reactive({
      username: '',
      password: '',
      code: '',
      uuid: '',
      rememberMe: true
    })

    const registerForm = reactive({
      username: '',
      password: ''
    })

    // 方法
    const lr_change = () => {
      isActive.value = !isActive.value
    }

    const login = async () => {
      // 表单验证
      if (!loginForm.username.trim()) {
        ElMessage.warning('账号不能为空')
        return
      }
      if (!loginForm.password.trim()) {
        ElMessage.warning('密码不能为空')
        return
      }
      if (!loginForm.code.trim()) {
        ElMessage.warning('验证码不能为空')
        return
      }

      loginLoading.value = true

      try {
        // 记住密码处理
        if (loginForm.rememberMe) {
          Cookies.set('username', loginForm.username, { expires: 30 })
          Cookies.set('password', EncryptedPassword(loginForm.password), { expires: 30 })
          Cookies.set('rememberMe', loginForm.rememberMe, { expires: 30 })
        } else {
          Cookies.remove('username')
          Cookies.remove('password')
          Cookies.remove('rememberMe')
        }

        // 执行登录
        await userStore.login(loginForm)

        // 获取用户信息
        await userStore.getUserInfo()

        ElMessage.success('登录成功')

        // 跳转
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败，请重试')
        // 刷新验证码
        getCode()
      } finally {
        loginLoading.value = false
      }
    }

    const handleRegister = async () => {
      if (!registerForm.username.trim()) {
        ElMessage.warning('账号不能为空')
        return
      }
      if (!registerForm.password.trim()) {
        ElMessage.warning('密码不能为空')
        return
      }
      if (registerForm.password.length < 6) {
        ElMessage.warning('密码至少6位字符')
        return
      }

      registerLoading.value = true

      try {
        await register(registerForm)
        ElMessage.success('注册成功')

        // 注册成功后切换到登录
        isActive.value = true
        loginForm.username = registerForm.username

        // 清空注册表单
        registerForm.username = ''
        registerForm.password = ''
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(error.message || '注册失败，请重试')
      } finally {
        registerLoading.value = false
      }
    }

    const getCode = async () => {
      try {
        const res = await getCodeImg()
        codeUrl.value = 'data:image/gif;base64,' + res.data.img
        loginForm.uuid = res.data.uuid
      } catch (error) {
        console.error('获取验证码失败:', error)
        ElMessage.error('获取验证码失败')
      }
    }

    const getCookie = () => {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')

      if (username) loginForm.username = username
      if (password) loginForm.password = DecryptPassword(password)
      if (rememberMe) loginForm.rememberMe = Boolean(rememberMe)
    }

    // 初始化
    const initializePage = async () => {
      try {
        // 异步加载验证码和Cookie
        await Promise.all([getCode(), nextTick(() => getCookie())])
      } catch (error) {
        console.error('页面初始化失败:', error)
      } finally {
        // 延迟一下让页面更流畅
        setTimeout(() => {
          pageLoading.value = false
        }, 300)
      }
    }

    // 生命周期
    onMounted(() => {
      initializePage()
    })

    return {
      isActive,
      pageLoading,
      loginLoading,
      registerLoading,
      codeUrl,
      loginForm,
      registerForm,
      lr_change,
      login,
      register: handleRegister,
      getCode
    }
  }
}
</script>

<style scoped lang="scss">
.login-page {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 轻量级背景动画 */
.background-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.animated-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  left: 80%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 80%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 30%;
  left: 70%;
  animation-delay: 1s;
}

.shape-5 {
  width: 140px;
  height: 140px;
  top: 10%;
  left: 60%;
  animation-delay: 3s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-20px) rotate(120deg);
  }
  66% {
    transform: translateY(20px) rotate(240deg);
  }
}

/* 主要内容区域 */
.lr-container {
  width: 100%;
  height: 100vh;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.content_body {
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  color: #fff;
  width: 400px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.1);
  padding: 40px 20px;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }

  .content_header {
    text-align: center;
    margin-bottom: 30px;

    a {
      font-size: 20px;
      font-weight: 500;
      margin: 0 20px;
      cursor: pointer;
      color: rgba(255, 255, 255, 0.7);
      transition: all 0.3s ease;
      padding: 8px 0;
      border-bottom: 2px solid transparent;

      &:hover {
        color: #fff;
      }
    }

    .isActive {
      color: #fea443 !important;
      font-weight: 600;
      border-bottom-color: #fea443;
    }
  }

  .login_content,
  .register_content {
    text-align: center;

    .ipt {
      width: 100%;
      height: 50px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 8px;
      padding: 0 16px;
      margin: 12px 0;
      font-size: 14px;
      background: rgba(255, 255, 255, 0.1);
      color: #fff;
      backdrop-filter: blur(5px);
      transition: all 0.3s ease;

      &::placeholder {
        color: rgba(255, 255, 255, 0.6);
      }

      &:focus {
        outline: none;
        border-color: #fea443;
        box-shadow: 0 0 0 2px rgba(254, 164, 67, 0.2);
        background: rgba(255, 255, 255, 0.15);
      }
    }

    .login_btn,
    .register_btn {
      width: 100%;
      height: 50px;
      color: #fff;
      margin: 20px 0 10px;
      border: none;
      font-size: 16px;
      font-weight: 600;
      border-radius: 8px;
      background: linear-gradient(45deg, #fea443, #ff6b6b);
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(254, 164, 67, 0.3);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
        transform: none;
      }
    }

    .rpwd {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      margin: 15px 0;
      color: rgba(255, 255, 255, 0.8);

      input[type='checkbox'] {
        margin-right: 8px;
        transform: scale(1.2);
      }
    }
  }

  .identify-code {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    margin: 12px 0;

    .code-input {
      flex: 1;
      height: 50px;
    }

    .login-code {
      .login-code-img {
        height: 50px;
        width: 120px;
        border-radius: 6px;
        cursor: pointer;
        border: 1px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;

        &:hover {
          border-color: #fea443;
        }
      }

      .code-loading {
        height: 50px;
        width: 120px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 6px;
        background: rgba(255, 255, 255, 0.1);
        color: rgba(255, 255, 255, 0.6);
        font-size: 12px;
      }
    }
  }
}

/* 加载状态 */
.page-loading {
  text-align: center;
  padding: 60px 20px;

  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(255, 255, 255, 0.3);
    border-top: 4px solid #fea443;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
  }

  p {
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    margin: 0;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content_body {
    width: 90vw;
    padding: 30px 15px;
  }

  .content_header a {
    font-size: 18px;
    margin: 0 15px;
  }

  .identify-code {
    flex-direction: column;
    gap: 8px;

    .login-code .login-code-img,
    .login-code .code-loading {
      width: 100%;
    }
  }
}

/* 提升性能的优化 */
* {
  will-change: auto;
}

.shape {
  will-change: transform;
}

.content_body {
  will-change: transform;
}
</style>
