<template>
  <div class="bili-comment-item">
    <!-- 主评论区域 -->
    <div class="comment-main">
      <el-avatar class="user-avatar" :src="getAvatarUrl(comment)" />

      <div class="comment-body">
        <!-- 用户信息和内容 -->
        <div class="comment-info">
          <span class="username">{{ getUserName(comment) }}</span>
          <span class="comment-content">{{ comment.content }}</span>
        </div>

        <!-- 操作栏 -->
        <div class="comment-meta">
          <span class="time">{{ formatTime(comment.createTime) }}</span>

          <div class="actions">
            <!-- 点赞 -->
            <button
              class="action-btn like-btn"
              :class="{ 'is-liked': isLiked }"
              @click="handleLike"
              :disabled="likeLoading"
            >
              <i class="icon-like"></i>
              <span v-if="currentLikeCount > 0">{{ currentLikeCount }}</span>
            </button>

            <!-- 回复 -->
            <button
              v-if="!isOwnComment"
              class="action-btn reply-btn"
              :class="{ 'is-active': showReplyInput }"
              @click="toggleReply"
            >
              回复
            </button>

            <!-- 删除 -->
            <button v-if="canDelete" class="action-btn delete-btn" @click="handleDelete">
              删除
            </button>
          </div>
        </div>

        <!-- 回复输入框 -->
        <div v-if="showReplyInput" class="reply-input-area">
          <div class="reply-input-box">
            <el-avatar class="reply-avatar" :src="userStore.avatar || defaultAvatar" />
            <div class="input-wrapper">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="3"
                :placeholder="`回复 @${getUserName(comment)}：`"
                class="reply-textarea"
                maxlength="500"
                show-word-limit
              />
              <div class="reply-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button
                  type="primary"
                  size="small"
                  @click="submitReply"
                  :loading="replyLoading"
                  :disabled="!replyContent.trim()"
                >
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 回复列表 -->
    <div v-if="replies && replies.length > 0" class="replies-section">
      <div class="replies-container">
        <!-- 显示的回复 -->
        <div v-for="reply in visibleReplies" :key="reply.id" class="reply-item">
          <el-avatar class="reply-avatar" :src="getAvatarUrl(reply)" />
          <div class="reply-content">
            <div class="reply-info">
              <span class="reply-username">{{ getUserName(reply) }}</span>
              <!-- 如果是对某个回复的回复，显示目标用户 -->
              <span v-if="reply.replyTo" class="reply-target">
                回复 <span class="target-name">@{{ reply.replyTo.userName }}</span
                >：
              </span>
              <span class="reply-text">{{ reply.content }}</span>
            </div>
            <div class="reply-meta">
              <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
              <button
                v-if="!isOwnReply(reply)"
                class="reply-action-btn"
                @click="replyToReply(reply)"
              >
                回复
              </button>
              <button
                v-if="canDeleteReply(reply)"
                class="reply-action-btn delete"
                @click="deleteReply(reply.id)"
              >
                删除
              </button>
            </div>
          </div>
        </div>

        <!-- 展开/收起控制 -->
        <div v-if="replies.length > maxVisibleReplies" class="expand-control">
          <button class="expand-btn" @click="toggleExpand">
            <span v-if="isExpanded">收起回复</span>
            <span v-else>
              共{{ replies.length }}条回复，
              <span class="expand-text">点击查看</span>
            </span>
            <i class="expand-icon" :class="{ 'is-expanded': isExpanded }"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { likeComment, getLikeStatus, addComment, deleteComment } from '@/api/comment.js'
import useUserStore from '@/stores/userStore'

// Props
const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  operaId: {
    type: Number,
    required: true
  },
  allComments: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['refresh', 'reply', 'delete'])

// Store
const userStore = useUserStore()

// 响应式数据
const isLiked = ref(false)
const currentLikeCount = ref(0)
const likeLoading = ref(false)
const showReplyInput = ref(false)
const replyContent = ref('')
const replyLoading = ref(false)
const isExpanded = ref(false)
const maxVisibleReplies = ref(3)

// 计算属性
const defaultAvatar = computed(
  () => 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
)

const isOwnComment = computed(() => {
  return userStore.userId && props.comment?.userId === userStore.userId
})

const canDelete = computed(() => {
  return (
    userStore.userId &&
    (props.comment?.userId === userStore.userId || userStore.hasPermission('comment:delete'))
  )
})

const replies = computed(() => {
  if (!props.comment?.replies) return []
  return props.comment.replies.map((reply) => {
    // 查找回复目标
    const replyTo = findReplyTarget(reply)
    return {
      ...reply,
      replyTo
    }
  })
})

const visibleReplies = computed(() => {
  if (isExpanded.value) return replies.value
  return replies.value.slice(0, maxVisibleReplies.value)
})

// 方法
const getAvatarUrl = (commentData) => {
  return commentData?.userAvatar || commentData?.avatar || defaultAvatar.value
}

const getUserName = (commentData) => {
  return commentData?.userName || commentData?.username || '匿名用户'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

const findReplyTarget = (reply) => {
  if (!reply.parentId || reply.parentId === props.comment.id) {
    return null // 直接回复主评论
  }

  // 查找被回复的评论
  return replies.value.find((r) => r.id === reply.parentId)
}

const isOwnReply = (reply) => {
  return userStore.userId && reply.userId === userStore.userId
}

const canDeleteReply = (reply) => {
  return (
    userStore.userId &&
    (reply.userId === userStore.userId || userStore.hasPermission('comment:delete'))
  )
}

// 点赞相关
const handleLike = async () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    return
  }

  likeLoading.value = true
  try {
    const response = await likeComment(props.comment.id, userStore.userId)
    if (response.code === 200) {
      isLiked.value = !isLiked.value
      currentLikeCount.value += isLiked.value ? 1 : -1
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

// 回复相关
const toggleReply = () => {
  showReplyInput.value = !showReplyInput.value
  if (showReplyInput.value) {
    replyContent.value = ''
  }
}

const cancelReply = () => {
  showReplyInput.value = false
  replyContent.value = ''
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }

  replyLoading.value = true
  try {
    const replyData = {
      content: replyContent.value.trim(),
      userId: userStore.userId,
      operaId: props.operaId,
      parentId: props.comment.id
    }

    const response = await addComment(replyData)
    if (response.code === 200) {
      ElMessage.success('回复成功')
      cancelReply()
      emit('refresh')
    } else {
      ElMessage.error(response.msg || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

const replyToReply = (targetReply) => {
  showReplyInput.value = true
  replyContent.value = `@${getUserName(targetReply)} `
  // 可以在这里设置特殊的回复目标标识
}

// 删除相关
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })

    const response = await deleteComment(props.comment.id, userStore.userId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      emit('refresh')
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const deleteReply = async (replyId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条回复吗？', '提示', {
      type: 'warning'
    })

    const response = await deleteComment(replyId, userStore.userId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      emit('refresh')
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 展开控制
const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
}

// 初始化
onMounted(async () => {
  currentLikeCount.value = props.comment?.likeCount || 0

  if (userStore.userId) {
    try {
      const response = await getLikeStatus(props.comment.id, userStore.userId)
      if (response.code === 200) {
        isLiked.value = response.data
      }
    } catch (error) {
      console.error('获取点赞状态失败:', error)
    }
  }
})
</script>

<style lang="scss" scoped>
.bili-comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #e3e5e7;

  &:last-child {
    border-bottom: none;
  }
}

.comment-main {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-info {
  margin-bottom: 8px;

  .username {
    font-weight: 500;
    color: #18191c;
    margin-right: 8px;
    font-size: 13px;
  }

  .comment-content {
    color: #18191c;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-word;
  }
}

.comment-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;

  .time {
    font-size: 12px;
    color: #9499a0;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 16px;
  }
}

.action-btn {
  background: none;
  border: none;
  padding: 0;
  font-size: 12px;
  color: #9499a0;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;

  &:hover {
    color: #00aeec;
  }

  &.is-liked {
    color: #fb7299;
  }

  &.is-active {
    color: #00aeec;
  }
}

.like-btn {
  .icon-like {
    width: 14px;
    height: 14px;
    background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>')
      no-repeat center;
    background-size: contain;
  }
}

.reply-input-area {
  margin-top: 12px;
  padding: 12px;
  background: #f6f7f8;
  border-radius: 6px;
}

.reply-input-box {
  display: flex;
  gap: 8px;
}

.reply-avatar {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.input-wrapper {
  flex: 1;
}

.reply-textarea {
  :deep(.el-textarea__inner) {
    border: 1px solid #c9ccd0;
    border-radius: 4px;
    font-size: 12px;
    resize: none;

    &:focus {
      border-color: #00aeec;
    }
  }
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.replies-section {
  margin-top: 12px;
  margin-left: 52px;
}

.replies-container {
  background: #f6f7f8;
  border-radius: 6px;
  padding: 12px;
}

.reply-item {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.reply-content {
  flex: 1;
  min-width: 0;
}

.reply-info {
  margin-bottom: 4px;

  .reply-username {
    font-weight: 500;
    color: #18191c;
    font-size: 12px;
    margin-right: 4px;
  }

  .reply-target {
    font-size: 12px;
    color: #9499a0;
    margin-right: 4px;

    .target-name {
      color: #00aeec;
    }
  }

  .reply-text {
    color: #18191c;
    font-size: 12px;
    line-height: 1.5;
  }
}

.reply-meta {
  display: flex;
  align-items: center;
  gap: 12px;

  .reply-time {
    font-size: 11px;
    color: #9499a0;
  }
}

.reply-action-btn {
  background: none;
  border: none;
  padding: 0;
  font-size: 11px;
  color: #9499a0;
  cursor: pointer;

  &:hover {
    color: #00aeec;
  }

  &.delete:hover {
    color: #fb7299;
  }
}

.expand-control {
  text-align: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #e3e5e7;
}

.expand-btn {
  background: none;
  border: none;
  color: #00aeec;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 0 auto;

  .expand-text {
    text-decoration: underline;
  }

  .expand-icon {
    width: 12px;
    height: 12px;
    background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M7 10l5 5 5-5z"/></svg>')
      no-repeat center;
    background-size: contain;
    transition: transform 0.2s;

    &.is-expanded {
      transform: rotate(180deg);
    }
  }
}
</style>
