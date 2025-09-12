<template>
  <div ref="vantaBg" class="vanta-container"></div>
  <div class="lr-container">
    <div class="content_body">
      <div class="content_header">
        <a :class="isActive ? ['isActive'] : []" @click="lr_change">登录</a>
        <a :class="isActive ? [] : ['isActive']" @click="lr_change">注册</a>
      </div>
      <!-- 登录 -->
      <div class="login_content" v-if="isActive">
        <el-form :model="loginForm">
          <div class="uname">
            <input type="text" class="ipt" placeholder="账号" v-model="loginForm.username" />
          </div>
          <div class="pwd">
            <input type="password" class="ipt" placeholder="密码" v-model="loginForm.password" />
          </div>

          <div @click="refreshCode" class="identify-code">
            <input
              type="text"
              class="ipt code-input"
              placeholder="请输入验证码"
              v-model="loginForm.code"
              @keyup.enter="login"
            />
            <!-- <VerificationCode :identifyCode="identifyCode" :refreshCode="refreshCode"></VerificationCode> -->
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img" />
            </div>
          </div>
          <div class="rpwd">
            <input type="checkbox" v-model="loginForm.rememberMe" />
            <span>记住密码</span>
          </div>
        </el-form>
        <button class="login_btn" @click="login">登录</button>
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
        <button class="register_btn" @click="register">注册</button>
      </div>
    </div>
  </div>
</template>

<script>
import * as THREE from 'three'
import BIRDS from 'vanta/dist/vanta.birds.min.js'
import VerificationCode from '@/components/Login/identify.vue'
import { getCodeImg } from '@/api/login'
import Cookies from 'js-cookie'
import { EncryptedPassword, DecryptPassword } from '@/utils/jsencrypt.js'
import useUserStore from '@/stores/userStore'
import { register } from '@/api/login'
export default {
  components: {
    VerificationCode
  },
  data() {
    return {
      antaEffect: null,
      isActive: true,
      identifyCode: '',
      loginForm: {
        username: '',
        password: '',
        code: '',
        uuid: '',
        rememberMe: true
      },
      registerForm: {
        username: '',
        password: ''
      },
      codeUrl: '',
      loading: false,
      // captchaEnabled:true,
      redirect: undefined
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  computed: {
    userStore() {
      return useUserStore() // 通过 computed 获取 store 实例
    }
  },
  methods: {
    lr_change() {
      this.isActive = !this.isActive
    },
    login() {
      if (this.loginForm.rememberMe) {
        Cookies.set('username', this.loginForm.username, { expires: 30 }) //30天
        Cookies.set('password', EncryptedPassword(this.loginForm.password), { expires: 30 })
        Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
      } else {
        Cookies.remove('username')
        Cookies.remove('password')
        Cookies.remove('rememberMe')
      }
      // TODO 需要对输入的账号和密码校验,还需要添加一些正则表达式
      if (!this.loginForm.username) {
        window.alert('账号不能为空')
        return
      }
      if (!this.loginForm.password) {
        window.alert('密码不能为空')
        return
      }
      if (!this.loginForm.code) {
        window.alert('验证码不能为空')
        return
      }
      // 调用userStore 的action的登陆方法
      this.userStore.login(this.loginForm).then(() => {
        // 登陆成功之后，调用 userstore 设置用户信息
        this.userStore.getUserInfo().then((res) => {
          // 检查是否有重定向路径
          const redirect = this.$route.query.redirect || '/'
          this.$router.push(redirect)
        })
      })
    },
    register() {
      register(this.registerForm).then(() => {
        window.alert('注册成功')
      })
    },
    refreshCode(newIdentifyCode) {
      this.identifyCode = newIdentifyCode
    },
    getCode() {
      getCodeImg().then((res) => {
        this.codeUrl = 'data:image/gif;base64,' + res.data.img
        this.loginForm.uuid = res.data.uuid
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : DecryptPassword(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    }
  },
  mounted() {
    try {
      this.vantaEffect = BIRDS({
        el: this.$refs.vantaBg,
        THREE: THREE,
        mouseControls: true,
        touchControls: true,
        gyroControls: false,
        minHeight: 200.0,
        minWidth: 200.0,
        scale: 1.0,
        scaleMobile: 1.0,
        backgroundColor: 0xffffff,
        color1: 0x9f2b2b,
        color2: 0x3e793c,
        birdSize: 0.5,
        wingSpan: 23.0,
        separation: 27.0,
        alignment: 37.0,
        cohesion: 55.0
      })
    } catch (error) {
      console.error('[vanta.js] birds init error: ', error)
    }
  },
  beforeUnmount() {
    if (this.vantaEffect) this.vantaEffect.destroy()
  }
}
</script>

<style scoped lang="scss">
.vanta-container {
  width: 100%;
  height: 100vh;
  position: absolute;
  z-index: 1;
}
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
  border: 1px solid #eee;
  box-shadow: 2px 3px 5px #eee;
  border-radius: 10px;
  color: #fff;
  width: 20%;
  background: rgba($color: #000000, $alpha: 0.2);
  .content_header {
    margin: 20px;
    a {
      font-size: 20px;
      margin: 0 10px;
      cursor: pointer;
    }
    .isActive {
      color: #fea443;
      font-weight: 400;
      border-bottom: 3px solid #fea443;
    }
  }
  .login_content,
  .register_content {
    text-align: center;
    .ipt {
      width: 80%;
      height: 60px;
      border: none;
      border-radius: 10px;
      padding: 0 20px;
      margin: 10px 0;
      font-size: 12px;
      &:focus {
        outline: none;
      }
    }
    .login_btn,
    .register_btn {
      width: 80%;
      height: 50px;
      color: #fff;
      margin: 10px 0;
      border: none;
      font-size: 14px;
      font-weight: 500;
      border-radius: 5px;
      background-color: rgba($color: #000000, $alpha: 0.2);
      cursor: pointer;
      &:hover {
        background-color: #fea443;
      }
    }
    .rpwd {
      display: flex;
      font-size: 12px;
      margin-left: 10%;
    }
  }
  .identify-code {
    // @include flex-center;
    display: flex;
    justify-content: space-around;
    padding: 0 10px;
    .code-input {
      height: 40px;
      width: 40%;
    }
    .login-code {
      padding: 10px;
      img {
        height: 40px;
        width: 70px;
      }
    }
  }
}
</style>
