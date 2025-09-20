// 戏曲类型常量
export const OPERA_TAGS = [
  '京剧',
  '黄梅戏',
  '秦腔',
  '曲剧',
  '晋剧',
  '评剧',
  '豫剧',
  '吕剧',
  '昆曲',
  '越剧',
  '潮剧',
  '川剧',
  '琼剧',
  '茂腔',
  '蒲剧',
  '越调',
  '赣剧',
  '湘剧'
]

// 用户角色常量
export const USER_ROLES = {
  SUPER_ADMIN: 'super_admin',
  ADMIN: 'admin',
  USER: 'user'
}

// 文件上传限制
export const UPLOAD_LIMITS = {
  IMAGE_MAX_SIZE: 5 * 1024 * 1024, // 5MB
  VIDEO_MAX_SIZE: 100 * 1024 * 1024, // 100MB
  AUDIO_MAX_SIZE: 50 * 1024 * 1024, // 50MB
  ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'],
  ALLOWED_VIDEO_TYPES: ['video/mp4', 'video/avi', 'video/mov', 'video/wmv'],
  ALLOWED_AUDIO_TYPES: ['audio/mp3', 'audio/wav', 'audio/ogg', 'audio/m4a']
}

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100]
}

export const ADMIN_ROLE = 1
export const USER_ROLE = 0
