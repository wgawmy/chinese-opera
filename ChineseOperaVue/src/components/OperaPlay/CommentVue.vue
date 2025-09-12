<!-- filepath: src/components/OperaPlay/CommentVue.vue -->
<template>
  <div class="comment-container">
    <!-- 评论输入区域 -->
    <div class="my-reply" v-if="userStore.token">
      <el-avatar class="header-img" :src="userStore.avatar || defaultAvatar"></el-avatar>
      <div class="reply-info">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="说点什么..."
          class="comment-input"
          @focus="handleInputFocus"
          @blur="handleInputBlur"
          maxlength="500"
          show-word-limit
        />
      </div>
      <div class="reply-btn-box" v-show="showSubmitBtn">
        <el-button @click="handleCancel" size="small">取消</el-button>
        <el-button
          @click="handleSubmitComment"
          type="primary"
          size="small"
          :loading="submitting"
          :disabled="!commentContent.trim()"
        >
          发布
        </el-button>
      </div>
    </div>

    <!-- 未登录提示 -->
    <div class="login-tip" v-else>
      <p>
        <el-icon><User /></el-icon>
        <span>登录后即可发表评论</span>
        <el-button type="primary" link @click="goToLogin">立即登录</el-button>
      </p>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list-wrapper">
      <!-- B站风格评论列表 -->
      <BilibiliCommentList
        :comments="comments"
        :loading="loading"
        :opera-id="operaId"
        @refresh="getComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getCommentsByOperaId, addComment } from '@/api/comment.js'
import BilibiliCommentList from './BilibiliCommentList.vue'
import useUserStore from '@/stores/userStore'

// Props
const props = defineProps({
  operaId: {
    type: Number,
    required: true
  }
})

// 组合式 API
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const commentContent = ref('')
const showSubmitBtn = ref(false)
const submitting = ref(false)
const loading = ref(false)
const comments = ref([])
const useCollapsibleMode = ref(false) // 保留备用，但暂时不使用

// 计算属性
const defaultAvatar = computed(
  () => 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
)

// 方法
const getComments = async () => {
  if (!props.operaId) {
    console.warn('operaId 为空，无法获取评论')
    return
  }

  loading.value = true
  console.log('开始获取评论，operaId:', props.operaId)

  try {
    const response = await getCommentsByOperaId(props.operaId)
    console.log('获取评论成功，响应数据:', response)

    // 根据后端接口的ResponseResult格式处理数据
    if (response.code === 200) {
      comments.value = response.data || []
      console.log('处理后的评论数据:', comments.value)
    } else {
      console.error('获取评论失败:', response.msg)
      ElMessage.error(response.msg || '获取评论失败')
      comments.value = []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败，请检查网络连接')
    comments.value = []
  } finally {
    loading.value = false
  }
}

const handleInputFocus = () => {
  showSubmitBtn.value = true
}

const handleInputBlur = () => {
  // 延迟隐藏，防止点击按钮时立即隐藏
  setTimeout(() => {
    if (!commentContent.value.trim()) {
      showSubmitBtn.value = false
    }
  }, 200)
}

const handleCancel = () => {
  commentContent.value = ''
  showSubmitBtn.value = false
}

const handleSubmitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    return
  }

  submitting.value = true
  try {
    const commentData = {
      content: commentContent.value.trim(),
      userId: userStore.userId,
      operaId: props.operaId,
      parentId: null // 顶级评论，回复评论时需要传入父评论ID
    }

    console.log('发布评论数据:', commentData)
    const response = await addComment(commentData)
    console.log('发布评论响应:', response)

    // 根据后端接口的ResponseResult格式处理响应
    if (response.code === 200) {
      ElMessage.success(response.msg || '评论发布成功')
      // 清空输入框并隐藏按钮
      commentContent.value = ''
      showSubmitBtn.value = false
      // 刷新评论列表
      await getComments()
    } else {
      ElMessage.error(response.msg || '评论发布失败')
    }
  } catch (error) {
    console.error('发布评论失败:', error)
    ElMessage.error('发布评论失败，请重试')
  } finally {
    submitting.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

// 切换评论模式（保留备用）
const handleModeChange = (value) => {
  ElMessage.info(value ? '已切换到智能折叠模式' : '已切换到传统模式')
  localStorage.setItem('commentMode', value ? 'collapsible' : 'traditional')
}

// 生命周期
onMounted(() => {
  getComments()
  // 读取用户偏好（保留备用）
  const savedMode = localStorage.getItem('commentMode')
  if (savedMode === 'collapsible') {
    useCollapsibleMode.value = true
  }
})

// 监听器
watch(
  () => props.operaId,
  (newOperaId, oldOperaId) => {
    if (newOperaId !== oldOperaId && newOperaId) {
      getComments()
    }
  }
)
</script>

<style lang="scss" scoped>
.comment-container {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.my-reply {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #e4e7ed;

  .header-img {
    width: 40px;
    height: 40px;
    margin-right: 12px;
    flex-shrink: 0;
  }

  .reply-info {
    flex: 1;
    margin-right: 12px;
  }

  .comment-input {
    :deep(.el-textarea__inner) {
      border: 1px solid #dcdfe6;
      border-radius: 6px;
      resize: none;

      &:focus {
        border-color: #409eff;
      }
    }
  }

  .reply-btn-box {
    display: flex;
    gap: 8px;
    align-items: flex-start;
    margin-top: 8px;
  }
}

.login-tip {
  padding: 40px 20px;
  text-align: center;
  background-color: #fafafa;
  border-bottom: 1px solid #e4e7ed;

  p {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin: 0;
    color: #909399;
    font-size: 14px;
  }

  .el-icon {
    font-size: 16px;
  }
}
</style>
