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
          >
            <!-- 头像部分 -->
            <div class="profile-avatar">
              <img
                src="https://img.shetu66.com/2023/04/25/1682391069844152.png"
                alt="用户头像"
                class="avatar-img"
              />
            </div>
            <!-- 下拉菜单  -->
            <div class="profile-dropdown" v-if="isDropdownVisible">
              <ul>
                <li><a href="#" @click="goTouserInfo">个人信息</a></li>
                <li><a href="#" @click="goToLogin">登录</a></li>
                <li><a href="#" @click="LoginOut">注销</a></li>
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
    }
  },
  methods: {
    showHeaderDropdown() {
      this.isDropdownVisible = true
    },
    hideHeaderDropdown() {
      this.isDropdownVisible = false
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
    LoginOut() {
      this.userStore.logOut().then(() => {
        this.$router.push({ name: 'Login' })
      })
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
      .profile-avatar:hover .avatar-img {
        transform: scale(1.5);
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
            transition: background-color 0.2s ease; // 添加过渡效果
            a {
              color: #333;
              display: block;
              text-decoration: none;
            }
          }

          li:last-child {
            border-bottom: none;
          }

          li:hover {
            background-color: rgba(0, 0, 0, 0.05);
          }
        }
      }
    }
  }
}
</style>
