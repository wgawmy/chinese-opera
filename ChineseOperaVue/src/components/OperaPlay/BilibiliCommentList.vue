<template>
  <div class="bili-comment-list">
    <!-- Loading 状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-skeleton">
        <div v-for="i in 3" :key="i" class="skeleton-item">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-content">
            <div class="skeleton-line short"></div>
            <div class="skeleton-line long"></div>
            <div class="skeleton-line medium"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!comments.length" class="empty-comments">
      <div class="empty-icon">💬</div>
      <p class="empty-text">还没有评论，快来抢沙发吧~</p>
    </div>

    <!-- 评论列表 -->
    <div v-else class="comments-container">
      <!-- 评论统计 -->
      <div class="comment-header">
        <h3 class="comment-title">评论 {{ totalComments }}</h3>
        <div class="sort-options">
          <button
            class="sort-btn"
            :class="{ active: sortType === 'time' }"
            @click="changeSortType('time')"
          >
            按时间
          </button>
          <button
            class="sort-btn"
            :class="{ active: sortType === 'hot' }"
            @click="changeSortType('hot')"
          >
            按热度
          </button>
        </div>
      </div>

      <!-- 评论项列表 -->
      <div class="comment-items">
        <BilibiliCommentItem
          v-for="comment in sortedComments"
          :key="comment.id"
          :comment="comment"
          :opera-id="operaId"
          :all-comments="comments"
          @refresh="handleRefresh"
          @reply="handleReply"
          @delete="handleDelete"
        />
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more">
        <button class="load-more-btn" @click="loadMore" :disabled="loadingMore">
          {{ loadingMore ? '加载中...' : '点击查看更多评论' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import BilibiliCommentItem from './BilibiliCommentItem.vue'

// Props
const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  operaId: {
    type: Number,
    required: true
  },
  hasMore: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['refresh', 'loadMore'])

// 响应式数据
const sortType = ref('time') // 'time' | 'hot'
const loadingMore = ref(false)

// 计算属性
const totalComments = computed(() => {
  const countComments = (comments) => {
    let count = 0
    comments.forEach((comment) => {
      count += 1
      if (comment.replies && comment.replies.length > 0) {
        count += countComments(comment.replies)
      }
    })
    return count
  }
  return countComments(props.comments)
})

const sortedComments = computed(() => {
  const comments = [...props.comments]

  if (sortType.value === 'hot') {
    // 按热度排序（点赞数 + 回复数）
    return comments.sort((a, b) => {
      const aHot = (a.likeCount || 0) + (a.replies?.length || 0)
      const bHot = (b.likeCount || 0) + (b.replies?.length || 0)
      return bHot - aHot
    })
  } else {
    // 按时间排序
    return comments.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  }
})

// 方法
const changeSortType = (type) => {
  sortType.value = type
}

const handleRefresh = () => {
  emit('refresh')
}

const handleReply = (comment) => {
  console.log('回复评论:', comment)
}

const handleDelete = (commentId) => {
  console.log('删除评论:', commentId)
}

const loadMore = async () => {
  loadingMore.value = true
  try {
    emit('loadMore')
  } finally {
    loadingMore.value = false
  }
}
</script>

<style lang="scss" scoped>
.bili-comment-list {
  background: #fff;
  border-radius: 8px;
}

.loading-container {
  padding: 20px;
}

.loading-skeleton {
  .skeleton-item {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;

    .skeleton-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: skeleton-loading 1.5s infinite;
    }

    .skeleton-content {
      flex: 1;

      .skeleton-line {
        height: 12px;
        border-radius: 6px;
        background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
        background-size: 200% 100%;
        animation: skeleton-loading 1.5s infinite;
        margin-bottom: 8px;

        &.short {
          width: 30%;
        }
        &.medium {
          width: 60%;
        }
        &.long {
          width: 80%;
        }
      }
    }
  }
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.empty-comments {
  text-align: center;
  padding: 60px 20px;

  .empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.5;
  }

  .empty-text {
    color: #9499a0;
    font-size: 14px;
    margin: 0;
  }
}

.comments-container {
  padding: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e3e5e7;

  .comment-title {
    font-size: 16px;
    font-weight: 500;
    color: #18191c;
    margin: 0;
  }

  .sort-options {
    display: flex;
    gap: 8px;
  }

  .sort-btn {
    background: none;
    border: 1px solid #e3e5e7;
    border-radius: 4px;
    padding: 6px 12px;
    font-size: 12px;
    color: #61666d;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #00aeec;
      color: #00aeec;
    }

    &.active {
      background: #00aeec;
      border-color: #00aeec;
      color: #fff;
    }
  }
}

.comment-items {
  .bili-comment-item:last-child {
    border-bottom: none;
  }
}

.load-more {
  text-align: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e3e5e7;

  .load-more-btn {
    background: none;
    border: 1px solid #e3e5e7;
    border-radius: 4px;
    padding: 8px 24px;
    color: #61666d;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover:not(:disabled) {
      border-color: #00aeec;
      color: #00aeec;
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}
</style>
