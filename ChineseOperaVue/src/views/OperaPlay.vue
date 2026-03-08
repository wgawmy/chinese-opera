<template>
  <div class="play-container">
    <el-header style="padding: 0">
      <Header :showBackground="false" :show-logo="false"></Header>
    </el-header>
    <el-main style="padding: 0 60px; margin-top: 30px">
      <div class="opera-info">
        <h1 v-if="nameShow">{{ nameShow }}</h1>
        <h1 v-else>{{ opera.name }}</h1>
        <span>
          {{ opera.tag }}
        </span>
        <span>演唱者：{{ opera.singer }}</span>
      </div>
      <div class="iframe-container">
        <video
          controls
          :src="opera.playUrl"
          :key="opera.playUrl"
          width="70%"
          height="550px"
        ></video>
        <AsideList :operaList="operaList" @getChoicedOpera="changeCurrentOpera"></AsideList>
      </div>

      <div class="user-operate">
        <!-- 收藏和评分 -->
        <el-icon :size="20" class="star" @click="toggleCollect">
          <Star :style="{ color: isCollected ? 'yellow' : '' }" />
        </el-icon>
        <el-rate
          v-model="userScore"
          :max="5"
          @change="rateOpera"
          allow-half
          style="margin-left: 10px"
        ></el-rate>
      </div>
      <Comment v-if="opera" :operaId="opera.id"></Comment>
    </el-main>
  </div>
</template>

<script>
import Comment from '@/components/OperaPlay/CommentVue.vue'
import AsideList from '@/components/OperaPlay/AsideList.vue'
import Header from '@/components/Home/Header.vue'
import { getOperaByCollectionId, getOperaPlayUrl, getOperaListByTag } from '@/api/opera.js'
export default {
  name: 'OperaPlay',
  components: {
    Header,
    AsideList,
    Comment
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      opera: null,
      operaList: [],
      collection: '',
      nameShow: '',
      isCollected: false,
      userScore: 0
    }
  },
  created() {
    //从别的界面跳转过来的
    this.opera = JSON.parse(this.$route.query.opera)
    // 处理路径
    // this.opera.playUrl = getOperaPlayUrl(this.opera);
    //根据collectionId 从数据库当中查询。
    if (this.opera.collectionId) {
      this.getOperaListByConditionId(this.opera.collectionId)
    } else {
      this.getOperasByTag(this.opera.tag)
    }
    // 初始化收藏和评分状态（假设有API，实际需替换为真实接口）
    this.initCollectAndScore()
  },
  methods: {
    // 初始化收藏和评分状态（需替换为真实API）
    initCollectAndScore() {
      // 示例：假设有API getOperaUserStatus(operaId)
      // getOperaUserStatus(this.opera.id).then(res => {
      //   this.isCollected = res.collected
      //   this.userScore = res.score
      // })
      // 这里用默认值演示
      this.isCollected = false
      this.userScore = 0
    },
    toggleCollect() {
      // 收藏/取消收藏逻辑，需替换为真实API
      // collectOperaApi(this.opera.id, !this.isCollected).then(() => {
      //   this.isCollected = !this.isCollected
      //   this.$message.success(this.isCollected ? '已收藏' : '已取消收藏')
      // })
      this.isCollected = !this.isCollected
      this.$message.success(this.isCollected ? '已收藏' : '已取消收藏')
    },
    rateOpera(score) {
      // 评分逻辑，需替换为真实API
      // rateOperaApi(this.opera.id, score).then(() => {
      //   this.userScore = score
      //   this.$message.success(`评分成功：${score}分`)
      // })
      this.userScore = score
      this.$message.success(`评分成功：${score}分`)
    },
    // 根据collectid 获得属于相同组的戏曲
    getOperaListByConditionId(collectionId) {
      getOperaByCollectionId(collectionId).then((res) => {
        this.operaList = res.data.operaList
        this.collection = res.data.collection
        if (this.collection) {
          this.nameShow = this.collection.title
        } else {
          this.nameShow = this.opera.name
        }
      })
    },
    getOperasByTag(operaTag) {
      const operaParam = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        operaTag: operaTag
      }
      getOperaListByTag(operaParam).then((res) => {
        this.total = res.total
        this.operaList = res.records
      })
    },
    changeCurrentOpera(opera) {
      this.opera = opera
    }
  }
}
</script>

<style lang="scss" scoped>
.play-container {
  padding: 0;
}
.opera-info {
  h1 {
    font-size: 25px;
  }
  span {
    width: 50px;
    @include no-wrap;
    &:nth-of-type(1) {
      margin-right: 10px;
    }
  }
}
.star:hover {
  color: yellow;
}
.iframe-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>
