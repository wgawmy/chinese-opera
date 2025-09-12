<template>
  <div class="opera-card" :style="style" @click="navigateToOperaPlay" v-if="opera">
    <img
      class="opera-img"
      :src="opera.pictureUrl || '/default-opera.jpg'"
      :alt="opera.name || '戏曲'"
    />
    <div class="opera-info">
      <span class="opera-name">{{ opera.name || '未知戏曲' }}</span>
      <div class="opera-details">
        <span class="opera-singer">{{ opera.singer || '未知演员' }}</span>
        <span class="opera-tag">{{ opera.tag || '未分类' }}</span>
      </div>
    </div>
  </div>
  <div v-else class="opera-card loading" :style="style">
    <div class="loading-placeholder">加载中...</div>
  </div>
</template>

<script>
export default {
  props: {
    opera: {
      type: Object,
      required: true //表示父组件在向子组件传递的时候，必须传递一个名为opera的属性值给子组件。
    },
    width: {
      type: Number,
      default: 200
    },
    height: {
      type: Number,
      default: 200
    },
    style: {
      type: Object,
      default: () => ({})
    }
  },
  name: 'SingleOpera',

  methods: {
    navigateToOperaPlay() {
      if (!this.opera) {
        console.warn('Opera对象为空，无法导航')
        return
      }

      this.$router.push({
        name: 'operaPlay',
        query: { opera: JSON.stringify(this.opera) }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.opera-card {
  // width: 200px;
  .opera-img {
    width: 100%;
    height: 150px;
    object-fit: cover;
    border-radius: 12px;
    cursor: pointer;
    &:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }
  }
  .opera-info {
    margin-top: 0px;
    padding: 0px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .opera-name {
      display: inline-block;
      font-size: 12px;
      font-weight: bold;
      text-align: center;
      @include no-wrap;
    }
    .opera-details {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .opera-singer,
      .opera-tag {
        font-size: 12px;
        @include no-wrap;
      }
      .opera-singer {
        max-width: 80%;
      }
    }
  }

  &.loading {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f5f5;
    border-radius: 12px;

    .loading-placeholder {
      color: #999;
      font-size: 14px;
    }
  }
}
</style>
