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
        <!-- 点赞 -->

        <!-- 收藏 -->
        <!-- <el-icon :size="20" class="star">
              <Star/>
            </el-icon> -->
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
      nameShow: ''
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
  },
  methods: {
    collectOpera() {
      // 收藏逻辑
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
