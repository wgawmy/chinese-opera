<template>
  <div>
    <el-header style="padding: 0; height: auto">
      <HomeHeader></HomeHeader>
    </el-header>
    <el-main style="padding: 0 60px; height: 75vh">
      <!-- 分类列表 -->
      <div>
        <div class="header-channel">
          <div class="imgContainer">
            <div class="mp3-imgContainer" @click="navigateToAudio">
              <img src="@/assets/mp36.webp" alt="热门照片" />
              <span>MP3</span>
            </div>
            <div class="hot-imgContainer" @click="navigateToHotList">
              <img src="@/assets/hot1.webp" alt="热门照片" />
              <span>热门</span>
            </div>
          </div>
          <Channel></Channel>
        </div>
      </div>
      <div class="recommend-container">
        <div class="carousel-container">
          <el-carousel height="50vh">
            <el-carousel-item
              v-for="(opera, index) in carouselItems"
              :key="index"
              @click="navigateToOperaPlay(opera)"
            >
              <img
                :src="opera.pictureUrl"
                :alt="opera.name"
                class="carousel-img cover-img"
                title="热播榜"
              />
              <div class="carousel-operaName">
                <h3>{{ opera.name }}</h3>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="right-operas">
          <div
            class="singleOpera-container"
            :style="{ width: '200px', height: '200px' }"
            v-for="(opera, index) in operas"
            :key="index"
            @click="navigateToOperaPlay(opera)"
          >
            <SingleOpera :opera="opera"></SingleOpera>
          </div>
        </div>
      </div>
    </el-main>
    <Footer></Footer>
  </div>
</template>

<script>
import HomeHeader from '@/components/Home/Header.vue'
import Channel from '@/components/Home/ChannelVue.vue'
import Footer from '@/components/Home/Footer.vue'
import SingleOpera from '@/components/Home/SingleOpera.vue'
import { getAlloperaInfo, getOperaPlayUrl } from '@/api/opera.js'
export default {
  name: 'Home',
  components: {
    HomeHeader,
    Channel,
    SingleOpera,
    Footer
  },
  created() {
    this.getoperaInfo()
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      operaInfoList: []
    }
  },
  methods: {
    navigateToOperaPlay(opera) {
      this.$router.push({
        name: 'operaPlay',
        query: { opera: JSON.stringify(opera) }
      })
    },
    navigateToHotList() {
      this.$router.push({
        name: 'HotList'
      })
    },

    navigateToAudio() {
      this.$router.push({
        name: 'Audio'
      })
    },
    getoperaInfo() {
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      getAlloperaInfo(params).then((res) => {
        this.operaInfoList = res.data.records
        // this.operaInfoList = res.data.data.map(opera=>{
        //     opera.playUrl = getOperaPlayUrl(opera);
        //     return opera;
        // })
      })
    }
  },
  computed: {
    carouselItems() {
      return this.operaInfoList.slice(0, 4)
    },
    operas() {
      return this.operaInfoList.slice(4, 10)
    }
  }
}
</script>

<style lang="scss" scoped>
.header-channel {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;

  padding: 0 60px;

  .imgContainer {
    display: flex;
    width: 120px;
    justify-content: space-between;
    .mp3-imgContainer,
    .hot-imgContainer {
      display: flex;
      // margin-right: 20px;
      flex-direction: column;
      span {
        text-align: center;
      }
      & > img {
        height: 50px;
        border-radius: 50px;
      }
    }
  }
}

.recommend-container {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  .carousel-container {
    width: 30vw;
    position: relative;
    margin-left: 20px;
    margin-top: 10px;
    .carousel-img {
      border-radius: 10px; // 设置圆角
    }
    .carousel-operaName {
      position: absolute;
      bottom: 20px;
      left: 20px;
      color: white;
      background-color: rgba(0, 0, 0, 0.5);
      border-radius: 5px;
    }
  }
  .right-operas {
    margin-left: 50px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    white-space: nowrap;
    width: 50vw;
    .singleOpera-container {
      margin-top: 10px;
      min-width: 15vw;
    }
  }
}
</style>
