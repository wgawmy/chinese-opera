package com.example.springboot.service.Impl;

import com.example.springboot.domain.Comment;
import com.example.springboot.domain.DTO.CommentDTO;
import com.example.springboot.mapper.CommentMapper;
import com.example.springboot.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getCommentsByOperaId(Long operaId) {
        // 1. 获取所有评论（包含用户信息）
        List<Comment> allComments = commentMapper.selectCommentsWithUserInfo(operaId);

        if (allComments == null || allComments.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 构建层级结构
        Map<Long, Comment> commentMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        // 先添加所有评论到 map 中，并初始化回复列表
        for (Comment comment : allComments) {
            comment.setReplies(new ArrayList<>());
            commentMap.put(comment.getId(), comment);
        }

        // 构建父子关系
        for (Comment comment : allComments) {
            if (comment.getParentId() == null) {
                // 根评论
                rootComments.add(comment);
            } else {
                // 回复评论
                Comment parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                }
            }
        }

        // 按创建时间排序（最新的在前）
        rootComments.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));

        return rootComments;
    }

    @Override
    public void addComment(CommentDTO commentDTO) throws Exception {
        try {
            // 参数验证
            validateCommentDTO(commentDTO);

            // 检查父评论是否存在（如果是回复）
            if (commentDTO.getParentId() != null) {
                Comment parentComment = commentMapper.selectById(commentDTO.getParentId());
                if (parentComment == null || parentComment.getStatus() != 1) {
                    throw new Exception("父评论不存在或已被删除");
                }
            }

            // 创建评论对象
            Comment comment = new Comment();
            comment.setContent(commentDTO.getContent().trim());
            comment.setUserId(commentDTO.getUserId());
            comment.setOperaId(commentDTO.getOperaId());
            comment.setParentId(commentDTO.getParentId());
            comment.setLikeCount(0);
            comment.setStatus(1); // 1-正常状态

            // 插入评论
            commentMapper.insertComment(comment);

            // 更新戏曲的评论数统计
            commentMapper.updateOperaCommentCount(commentDTO.getOperaId());

        } catch (Exception e) {
            throw new Exception("添加评论失败: " + e.getMessage());
        }
    }

    @Override
    public boolean canDeleteComment(Long commentId, Long userId) {
        try {
            Comment comment = commentMapper.selectById(commentId);
            if (comment == null || comment.getStatus() != 1) {
                return false;
            }

            // 评论作者可以删除，或者管理员可以删除
            return comment.getUserId().equals(userId) || isAdmin(userId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteComment(Long commentId) throws Exception {
        try {
            Comment comment = commentMapper.selectById(commentId);
            if (comment == null) {
                throw new Exception("评论不存在");
            }

            // 软删除评论
            commentMapper.updateStatus(commentId, 0);

            // 同时软删除该评论的所有回复
            commentMapper.deleteRepliesByParentId(commentId);

            // 更新戏曲评论数统计
            commentMapper.updateOperaCommentCount(comment.getOperaId());

        } catch (Exception e) {
            throw new Exception("删除评论失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> toggleLike(Long commentId, Long userId) throws Exception {
        try {
            Map<String, Object> result = new HashMap<>();

            // 检查评论是否存在
            Comment comment = commentMapper.selectById(commentId);
            if (comment == null || comment.getStatus() != 1) {
                throw new Exception("评论不存在或已被删除");
            }

            // 检查是否已点赞
            boolean isLiked = commentMapper.checkLike(commentId, userId);

            if (isLiked) {
                // 取消点赞
                commentMapper.deleteLike(commentId, userId);
                commentMapper.decrementLikeCount(commentId);
                result.put("action", "unlike");
                result.put("message", "取消点赞");
            } else {
                // 添加点赞
                commentMapper.insertLike(commentId, userId);
                commentMapper.incrementLikeCount(commentId);
                result.put("action", "like");
                result.put("message", "点赞成功");
            }

            // 返回最新的点赞数
            Comment updatedComment = commentMapper.selectById(commentId);
            result.put("likeCount", updatedComment.getLikeCount());
            result.put("isLiked", !isLiked);

            return result;
        } catch (Exception e) {
            throw new Exception("点赞操作失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isLiked(Long commentId, Long userId) {
        try {
            return commentMapper.checkLike(commentId, userId);
        } catch (Exception e) {
            return false;
        }
    }







    // ===== 私有辅助方法 =====

    /**
     * 验证评论DTO参数
     */
    private void validateCommentDTO(CommentDTO commentDTO) throws Exception {
        if (commentDTO == null) {
            throw new Exception("评论数据不能为空");
        }
        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            throw new Exception("评论内容不能为空");
        }
        if (commentDTO.getContent().length() > 500) {
            throw new Exception("评论内容不能超过500字符");
        }
        if (commentDTO.getUserId() == null) {
            throw new Exception("用户ID不能为空");
        }
        if (commentDTO.getOperaId() == null) {
            throw new Exception("戏曲ID不能为空");
        }
    }

    /**
     * 检查是否为管理员
     */
    private boolean isAdmin(Long userId) {
        try {
            // 这里应该根据实际的权限系统来判断
            // 例如查询用户角色表
            return commentMapper.checkUserRole(userId, "admin") ||
                    commentMapper.checkUserRole(userId, "super_admin");
        } catch (Exception e) {
            return false;
        }
    }
}
