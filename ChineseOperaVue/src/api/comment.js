import request from '@/utils/request'

/**
 * 根据戏曲ID获取评论列表
 * @param {number} operaId - 戏曲ID
 * @returns {Promise} 返回评论列表
 */
export function getCommentsByOperaId(operaId) {
  return request({
    url: `/comments/${operaId}`,
    method: 'get'
  })
}

/**
 * 添加评论
 * @param {object} data - 评论数据
 * @param {string} data.content - 评论内容
 * @param {number} data.userId - 用户ID
 * @param {number} data.operaId - 戏曲ID
 * @param {number} [data.parentId] - 父评论ID (回复时传入)
 * @returns {Promise} 返回操作结果
 */
export function addComment(data) {
  return request({
    url: '/comments/addcomment',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 * @param {number} commentId - 评论ID
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回操作结果
 */
export function deleteComment(commentId, userId) {
  return request({
    url: `/comments/${commentId}`,
    method: 'delete',
    params: { userId }
  })
}

/**
 * 点赞/取消点赞评论
 * @param {number} commentId - 评论ID
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回操作结果
 */
export function likeComment(commentId, userId) {
  return request({
    url: `/comments/${commentId}/like`,
    method: 'post',
    params: { userId }
  })
}

/**
 * 获取用户对评论的点赞状态
 * @param {number} commentId - 评论ID
 * @param {number} userId - 用户ID
 * @returns {Promise} 返回点赞状态
 */
export function getLikeStatus(commentId, userId) {
  return request({
    url: `/comments/${commentId}/like-status`,
    method: 'get',
    params: { userId }
  })
}
