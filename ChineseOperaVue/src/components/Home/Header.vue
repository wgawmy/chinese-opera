<template>
  <div class="head-container" :class="showBackground ? 'with-bg' : 'no-bg'">
    <div class="head-bar">
      <ul class="left-entry">
        <li class="left-item" @click="navigateToHome">
          <a href="#" @click="goToHome">
            <span>首页</span>
          </a>
        </li>
        <li class="left-item" @click="goToShop">
          <a href="#">
            <span>商城</span>
          </a>
        </li>
        <li class="left-item">
          <a href="#">
            <span>戏小圈</span>
          </a>
        </li>
      </ul>
      <!-- 中心的搜索栏 -->
      <div class="center-search-container">
        <form id="nav-searchform">
          <div class="nav-search-content">
            <input class="nav-search-input" placeholder="" />
          </div>
          <div class="nav-search-btn">
            <svg
              width="17"
              height="17"
              viewBox="0 0 17 17"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                fill-rule="evenodd"
                clip-rule="evenodd"
                d="M16.3451 15.2003C16.6377 15.4915 16.4752 15.772 16.1934 16.0632C16.15 16.1279 16.0958 16.1818 16.0525 16.2249C15.7707 16.473 15.4456 16.624 15.1854 16.3652L11.6848 12.8815C10.4709 13.8198 8.97529 14.3267 7.44714 14.3267C3.62134 14.3267 0.5 11.2314 0.5 7.41337C0.5 3.60616 3.6105 0.5 7.44714 0.5C11.2729 0.5 14.3943 3.59538 14.3943 7.41337C14.3943 8.98802 13.8524 10.5087 12.8661 11.7383L16.3451 15.2003ZM2.13647 7.4026C2.13647 10.3146 4.52083 12.6766 7.43624 12.6766C10.3517 12.6766 12.736 10.3146 12.736 7.4026C12.736 4.49058 10.3517 2.1286 7.43624 2.1286C4.50999 2.1286 2.13647 4.50136 2.13647 7.4026Z"
                fill="currentColor"
              ></path>
            </svg>
          </div>
        </form>
      </div>
      <!-- 右侧的人物信息 -->
      <ul class="right-entry">
        <li class="head-avatar-wrap right-item">
          <div
            class="profile-container"
            @mouseover="showHeaderDropdown"
            @mouseleave="hideHeaderDropdown"
            @click="handleAvatarClick"
          >
            <!-- 头像部分 -->
            <div class="profile-avatar">
              <img
                :src="userAvatar"
                :alt="isLoggedIn ? '用户头像' : '默认头像'"
                class="avatar-img"
              />
              <!-- 显示用户名或登录状态 -->
              <span class="user-status" v-if="!isLoggedIn">点击登录</span>
            </div>
            <!-- 下拉菜单  -->
            <div class="profile-dropdown" v-if="isDropdownVisible">
              <ul>
                <!-- 已登录时显示的菜单 -->
                <template v-if="isLoggedIn">
                  <li class="user-info">
                    <span class="username">{{ displayUserName }}</span>
                  </li>
                  <li><a href="#" @click="goTouserInfo">个人信息</a></li>
                  <li>
                    <a
                      href="javascript:void(0)"
                      @click.stop.prevent="LoginOut"
                      onclick="console.log('原生click事件触发')"
                      >注销</a
                    >
                  </li>
                </template>
                <!-- 未登录时显示的菜单 -->
                <template v-else>
                  <li><a href="#" @click="goToLogin">登录</a></li>
                </template>
              </ul>
            </div>
          </div>
        </li>
        <!-- 收藏 -->
        <li class="right-item">
          <a>
            <span>收藏</span>
          </a>
        </li>
        <!-- 历史记录 -->
        <li class="right-item">
          <a @click="goToHistory">
            <span>历史</span>
          </a>
        </li>
        <li class="right-item">
          <a @click="goToTest">
            <span>弹窗</span>
          </a>
        </li>
        <li class="right-item">
          <a>
            <span>动态</span>
          </a>
        </li>
        <li class="right-item" @click="navigateToManage">
          <el-button type="danger" v-hasPermi="['manage:edit']">管理</el-button>
        </li>
      </ul>
    </div>
    <!-- 存放梨园撷英标志的地方 -->
    <div class="logo-container" v-if="showLogo">
      <img src="@/assets/logo.png" style="margin-left: 20px" />
    </div>
  </div>
</template>

<script>
import useUserStore from '@/stores/userStore.js'
import sunGif from '@/assets/sun.gif'
export default {
  name: 'HomeHeader',
  props: {
    showBackground: {
      type: Boolean,
      default: true
    },
    showLogo: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      isDropdownVisible: false,
      sungif: sunGif
    }
  },
  computed: {
    userStore() {
      return useUserStore()
    },
    // 判断是否已登录
    isLoggedIn() {
      return !!(this.userStore.token && this.userStore.userId)
    },
    // 获取用户头像，如果没有则使用默认头像
    userAvatar() {
      if (this.isLoggedIn && this.userStore.avatar) {
        return this.userStore.avatar
      }
      // 默认头像
      return 'https://img.shetu66.com/2023/04/25/1682391069844152.png'
    },
    // 获取用户名，如果没有则显示"未登录"
    displayUserName() {
      if (this.isLoggedIn && this.userStore.userName) {
        return this.userStore.userName
      }
      return '未登录'
    }
  },
  methods: {
    showHeaderDropdown() {
      this.isDropdownVisible = true
    },
    hideHeaderDropdown() {
      this.isDropdownVisible = false
    },
    // 处理头像点击事件
    handleAvatarClick() {
      // 如果未登录，直接跳转到登录页
      if (!this.isLoggedIn) {
        this.goToLogin()
      }
      // 如果已登录，保持下拉菜单行为
    },
    goToLogin() {
      this.$router.push({
        name: 'Login'
      })
    },
    goToHistory() {
      this.$router.push({
        name: 'History'
      })
    },
    goToTest() {
      this.$router.push({
        name: 'Test'
      })
    },
    goToHome() {
      this.$router.push({
        name: 'home'
      })
    },
    goToShop() {
      this.$router.push({
        name: 'Shop'
      })
    },
    async LoginOut() {
      // 防止下拉菜单隐藏
      this.isDropdownVisible = true

    //   alert('LoginOut方法被调用了！')
      console.log('=== 开始退出登录流程 ===')
      console.log('当前用户状态:', {
        token: this.userStore.token ? '存在' : '不存在',
        userId: this.userStore.userId,
        userName: this.userStore.userName
      })

      try {
        // 显示确认对话框
        console.log('显示确认对话框')
        await this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        console.log('用户确认退出，开始执行退出操作')

        // 直接调用userStore的logOut方法
        await this.userStore.logOut()

        console.log('退出登录操作完成')

        // 显示成功消息
        this.$message.success('退出登录成功')

        console.log('准备跳转到登录页')
        // 跳转到登录页
        await this.$router.push({ name: 'Login' })
        console.log('已跳转到登录页')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录过程中发生错误:', error)

          // 如果出现任何错误，确保清理本地状态
          console.log('强制清理用户状态')
          this.userStore.clearUserInfo()

          this.$message.warning('退出登录完成，本地状态已清理')

          // 跳转到登录页
          console.log('错误处理：跳转到登录页')
          await this.$router.push({ name: 'Login' })
        } else {
          console.log('用户取消了退出操作')
        }
      }

      console.log('=== 退出登录流程结束 ===')
    },
    navigateToManage() {
      this.$router.push({
        name: 'ManageData'
      })
    },
    navigateToHome() {
      this.$router.push({
        name: 'Home'
      })
    },
    goTouserInfo() {
      this.$router.push({
        name: 'UserInfo'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.head-container {
  background-size: cover;
  background-repeat: no-repeat;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); /* 阴影效果 */
  width: 100%;
  &.with-bg {
    background-image: url('@/assets/header-bg.png');
  }

  &.no-bg {
    background: $header_bg_color;
  }
}

.head-bar {
  height: 60px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  .left-entry {
    list-style-type: none; /*去掉li前面的黑点*/
    display: flex;
    width: 370px;
    .left-item {
      margin-right: 20px;
    }
  }
  .center-search-container {
    background-color: #f0f0f0; /* 半透明白色背景 */
    border-radius: 10px; /* 圆角 */
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 阴影效果 */
    width: 400px; /* 搜索框总宽度 */
    height: 40px;
    #nav-searchform {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .nav-search-input {
        width: 100%; /* 占据父容器的宽度 */
        padding: 0 10px;
        border: none; /* 去掉默认边框 */
        background-color: transparent;
        font-size: 15px;
        outline: none; /* 去掉点击时的高亮边框 */
      }

      /* 搜索按钮 */
      .nav-search-btn {
        cursor: pointer;
        height: 40px;
        padding: 0 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: transparent;
        border: none;
      }

      /* 搜索按钮里的 SVG 图标 */
      .nav-search-btn svg {
        // fill: #555; /* 灰色图标 */
        transition: fill 0.3s ease;
      }

      /* 鼠标悬停时的图标变色效果 */
    }
  }
  .right-entry {
    display: flex;
    justify-content: space-between;
    align-items: center;
    list-style-type: none; /*去掉li前面的黑点*/
    .right-item {
      margin-right: 20px;
    }
    .profile-container {
      position: relative;
      display: inline-block;
      cursor: pointer; /* 添加鼠标指针 */

      .profile-avatar {
        position: relative;

        .avatar-img {
          position: absolute;
          top: -25px;
          right: 0px;
          width: 40px;
          height: 40px;
          border-radius: 50%; /* 圆形头像 */
          border: 2px solid #fff; /* 白色边框 */
          transition: transform 0.3s ease;
        }

        .user-status {
          position: absolute;
          top: 20px;
          right: -20px;
          font-size: 12px;
          color: #666;
          white-space: nowrap;
          background: rgba(255, 255, 255, 0.9);
          padding: 2px 6px;
          border-radius: 4px;
          transition: opacity 0.3s ease;
        }
      }

      &:hover .avatar-img {
        transform: scale(1.1);
        z-index: 1001;
      }

      .profile-dropdown {
        position: absolute;
        top: 10px;
        right: -80px;
        background-color: white;
        width: 200px;
        z-index: 1000;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);

        ul {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            border-bottom: 1px solid #f0f0f0;
            padding: 10px;
            text-align: center;
            transition: background-color 0.2s ease;

            &.user-info {
              background-color: #f8f9fa;
              border-bottom: 2px solid #e9ecef;

              .username {
                font-weight: 600;
                color: #2c3e50;
                font-size: 14px;
              }
            }

            a {
              color: #333;
              display: block;
              text-decoration: none;
            }
          }

          li:last-child {
            border-bottom: none;
          }

          li:not(.user-info):hover {
            background-color: rgba(0, 0, 0, 0.05);
          }
        }
      }
    }
  }
}
</style>
