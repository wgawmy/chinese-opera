import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

// 统一消息提示
export const message = {
  success(msg) {
    ElMessage.success(msg)
  },
  error(msg) {
    ElMessage.error(msg)
  },
  warning(msg) {
    ElMessage.warning(msg)
  },
  info(msg) {
    ElMessage.info(msg)
  }
}

// 统一确认对话框
export const confirm = (message, title = '提示') => {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
}

// 统一通知
export const notify = {
  success(title, message) {
    ElNotification.success({
      title,
      message
    })
  },
  error(title, message) {
    ElNotification.error({
      title,
      message
    })
  },
  warning(title, message) {
    ElNotification.warning({
      title,
      message
    })
  },
  info(title, message) {
    ElNotification.info({
      title,
      message
    })
  }
}
