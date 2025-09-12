package com.example.springboot.service;

import com.example.springboot.domain.Comment;
import com.example.springboot.domain.DTO.CommentDTO;

import java.util.List;
import java.util.Map;


public interface CommentService {
    /**
     * 根据戏曲ID获取评论列表（包含回复的层级结构）
     *
     * @param operaId 戏曲ID
     * @return 评论列表
     */
    List<Comment> getCommentsByOperaId(Long operaId);

    /**
     * 添加评论
     *
     * @param commentDTO 评论数据传输对象
     * @throws Exception 添加失败时抛出异常
     */
    void addComment(CommentDTO commentDTO) throws Exception;

    /**
     * 检查用户是否可以删除指定评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return true-可以删除，false-不能删除
     */
    boolean canDeleteComment(Long commentId, Long userId);

    /**
     * 删除评论（软删除）
     *
     * @param commentId 评论ID
     * @throws Exception 删除失败时抛出异常
     */
    void deleteComment(Long commentId) throws Exception;

    /**
     * 点赞/取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 操作结果，包含点赞状态、点赞数等信息
     * @throws Exception 操作失败时抛出异常
     */
    Map<String, Object> toggleLike(Long commentId, Long userId) throws Exception;

    /**
     * 检查用户是否已点赞指定评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Long commentId, Long userId);

    /**
     * 根据评论ID获取评论详情
     *
     * @param commentId 评论ID
     * @return 评论详情
     */
}
