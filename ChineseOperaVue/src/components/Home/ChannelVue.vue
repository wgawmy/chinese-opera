<template>
  <ul class="opera-tags">
    <li
      v-for="(operaTag, index) in operaTags"
      :key="index"
      class="tag-item"
      :class="{ active: selectedTag === operaTag && isAudio }"
      @click="navigateToOperaTag(operaTag)"
    >
      {{ operaTag }}
      <span class="ripple"></span>
      <!-- 波纹效果 -->
    </li>
  </ul>
</template>

<script>
import { useTrackStore } from '@/stores/trackStore'
import { getAudioListByTag } from '@/api/audio.js'
export default {
  name: 'Channel',
  props: {
    isAudio: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // '越剧', '潮剧', , '琼剧', '茂腔', '蒲剧', '越调', '赣剧',
      operaTags: ['京剧', '黄梅戏', '豫剧', '川剧', '越剧', '秦腔', '昆曲', '湘剧', '评剧'],
      track: useTrackStore(),
      selectedTag: '京剧',
      pageNum: 1,
      pageSize: 10
    }
  },
  created() {
    if (this.isAudio) {
      this.operaTags = ['京剧', '黄梅戏', '川剧', '越剧', '秦腔', '评剧', '晋剧', '豫剧', '花鼓戏']
    }
  },
  methods: {
    navigateToOperaTag(operaTag) {
      this.selectedTag = operaTag
      this.track.setCurrentTag(operaTag)
      this.track.clearAudioList()
      if (this.isAudio) {
        this.getAudioList()
      } else {
        this.$router.push({
          name: 'OperaTagShow',
          query: { operaTag: operaTag }
        })
      }
    },

    // 向后端发送请求，更改store里面的值。
    async getAudioList() {
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        tag: this.selectedTag
      }
      try {
        const res = await getAudioListByTag(params)
        const audioList = res.records
        this.total = res.total
        this.track.addAudioList(audioList)
      } catch (error) {
        console.error('数据加载失败', error)
      }
    }
  }
}
</script>

<style scoped>
.opera-tags {
  display: grid;
  margin: 0 20px;
  grid-template-columns: repeat(9, 1fr);
  grid-gap: 10px 40px;
  list-style: none;
}
/* 基础标签样式 */
.tag-item {
  display: inline-block;
  padding: 5px 10px;
  margin: 5px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 20px;
  color: #333;
  text-align: center;
  position: relative;
  transition: all 0.3s ease;
  background: #f0f0f0; /* 灰白色背景 */
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 选中标签样式 */
.tag-item.active {
  background: linear-gradient(145deg, #e2e2e2, #d0d0d0); /* 选中后的蓝色背景 */
  color: white;
  transform: scale(1.1); /* 选中后稍微放大 */
  box-shadow: 0 0 15px rgba(4, 233, 249, 0.7); /* 选中时的光晕 */
}

/* 波纹效果 */
.tag-item .ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 300%;
  height: 300%;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0);
  animation: ripple-animation 0.6s ease-out;
  pointer-events: none;
}

/* 波纹动画 */
@keyframes ripple-animation {
  to {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0;
  }
}

/* 标签 Hover 时的动画 */
.tag-item:hover {
  background: linear-gradient(145deg, #e2e2e2, #d0d0d0); /* 灰色渐变 */
  transform: scale(1.05); /* 放大效果 */
  box-shadow: 0 0 20px rgba(200, 200, 200, 0.5); /* 增强的阴影效果 */
}

/* 标签文本的动感字体 */
.tag-item.active {
  font-family: 'Cursive', sans-serif;
  animation: bounce-text 0.5s ease-in-out infinite alternate;
}

/* 文字弹跳效果 */
@keyframes bounce-text {
  0% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0);
  }
}
</style>
