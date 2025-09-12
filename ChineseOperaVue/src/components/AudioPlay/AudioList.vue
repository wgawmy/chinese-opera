<template>
  <div class="opera-list flex-col">
    <div class="list-item list-header">
      <span class="list-name">戏曲名称</span>
      <span class="list-operation"></span>
    </div>
    <div class="list-content" @scroll="listScroll($event)">
      <!-- 双击实现 -->
      <div
        v-for="(audio, index) in track.audioList"
        :class="{ active: index == isChoicedAudioIndex }"
        :key="index"
        class="list-item"
        @dblclick="selectAudio(audio)"
      >
        <span class="list-name">{{ audio.name }}</span>

        <div class="icon-container">
          <el-icon class="action-icon" :size="30">
            <VideoPause v-if="track.isPlaying" @click="cancelAudio(audio)" />
            <VideoPlay v-if="!track.isPlaying" @click="selectAudio(audio)" />
          </el-icon>

          <el-icon class="action-icon"><Delete /></el-icon>
        </div>
      </div>
    </div>
    <!-- </template> -->
    <!-- 如果没有显示列表空空如也 -->
  </div>
</template>

<script>
import { useTrackStore } from '@/stores/trackStore'
import { getAudioListByTag } from '@/api/audio.js'
const THRESHOLD = 20
// 戏曲的不变的信息可以直接从后端获取，对于容易变化到的数据，需要放在vuex里面
export default {
  // props:{
  //   audioTag:{
  //     type:String,
  //     required:true
  //   }
  // },
  data() {
    return {
      lockUp: false,
      pageNum: 1,
      pageSize: 10,
      tag: '京剧',
      track: useTrackStore(),
      total: '',
      isChoicedAudioIndex: 0
    }
  },
  created() {},
  mounted() {
    this.getAudioList()
  },
  watch: {
    'track.currentTrackId': function (newTrackId) {
      const currentIndex = this.track.audioList.findIndex((track) => track.id === newTrackId)
      this.isChoicedAudioIndex = currentIndex
    },
    'track.currentTag': function (newTag) {
      this.track.clearAudioList()
      this.tag = newTag
      // this.getAudioList();
    }
  },
  methods: {
    selectAudio(audio) {
      // 1.先把当前的播放的戏曲id更换
      // 2.更换播放状态
      this.track.isPlaying = true

      const currentIndex = this.track.audioList.findIndex((track) => track.id === audio.id)
      this.isChoicedAudioIndex = currentIndex
      this.track.setCurrentTrackId(audio.id)
    },
    cancelAudio(audio) {
      this.track.isPlaying = false
      const currentIndex = this.track.audioList.findIndex((track) => track.id === audio.id)
      this.isChoicedAudioIndex = currentIndex
      this.track.setCurrentTrackId(audio.id)
    },
    //滚动事件
    listScroll(e) {
      const { scrollTop, scrollHeight, offsetHeight } = e.target
      if (
        scrollTop + offsetHeight >= scrollHeight - THRESHOLD &&
        !this.lockUp &&
        this.total > this.track.audioList.length
      ) {
        // console.log("滚动数据开始加载")
        this.lockUp = true
        this.loadMore()
      } else if (this.total == this.track.audioList.length) {
        // console.log("数据加载完毕...")
      }
    },
    loadMore() {
      this.pageNum += 1
      this.getAudioList()
      // this.lockUp = false;
    },
    async getAudioList() {
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        tag: this.tag
      }
      try {
        const res = await getAudioListByTag(params)
        const audioList = res.records
        this.total = res.total
        this.track.addAudioList(audioList)
      } catch (error) {
        console.error('数据加载失败', error)
      } finally {
        this.lockUp = false // 无论如何都要解锁
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.opera-list {
  width: 30vw;
  height: 75vh;
  padding: 10px;
  .list-content {
    overflow-x: hidden;
    overflow-y: auto;
  }
  .list-item {
    color: $text-color;
    @include flex-center();
    padding: 15px;
    border-bottom: 1px solid rgba(43, 41, 41, 0.2);
    transition: background-color 0.3s ease;
    & > span {
      text-align: center;
      @include no-wrap;
    }
    &:hover {
      background-color: #f0f0f0;
    }

    /* 鼠标悬停时，显示播放和删除按钮 */
    &:hover .action-icon {
      display: inline-block;
    }
    .icon-container {
      display: flex;
      justify-content: space-around;
      cursor: pointer;
      font-size: 24px; /* 调整图标大小 */
    }
  }
  .active {
    background: linear-gradient(145deg, #e2e2e2, #d0d0d0); /* 选中后的蓝色背景 */
    color: white;
    transform: scale(1.1); /* 选中后稍微放大 */
    // box-shadow: 0 0 15px rgba(249, 179, 4, 0.7);
  }
  .icon-container,
  .list-operation {
    flex-basis: 20%;
  }
  .list-name {
    flex-basis: 80%;
  }
  .list-header {
    color: $text_color;
    font-weight: bold;
    padding: 20px 0px;
  }
  .action-icon {
    display: none;
    cursor: pointer;
  }
}
</style>
