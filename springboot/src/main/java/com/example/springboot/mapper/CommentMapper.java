package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.domain.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据ID查询评论
     */
    @Select("SELECT c.*, u.nick_name as user_name, u.avatar as user_avatar " +
            "FROM comments c LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.id = #{commentId}")
    Comment selectById(@Param("commentId") Long commentId);

    /**
     * 查询评论列表（包含用户信息）
     */
    @Select("SELECT c.*, u.nick_name as user_name, u.avatar as user_avatar " +
            "FROM comments c LEFT JOIN sys_user u ON c.user_id = u.id " +
            "WHERE c.opera_id = #{operaId} AND c.status = 1 " +
            "ORDER BY c.create_time ASC")
    List<Comment> selectCommentsWithUserInfo(@Param("operaId") Long operaId);

    /**
     * 插入评论
     */
    @Insert("INSERT INTO comments (content, user_id, opera_id, parent_id, like_count, status, create_time) " +
            "VALUES (#{content}, #{userId}, #{operaId}, #{parentId}, #{likeCount}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertComment(Comment comment);

    /**
     * 更新评论状态
     */
    @Update("UPDATE comments SET status = #{status}, update_time = NOW() WHERE id = #{commentId}")
    void updateStatus(@Param("commentId") Long commentId, @Param("status") Integer status);



    /**
     * 删除父评论下的所有回复
     */
    @Update("UPDATE comments SET status = 0, update_time = NOW() WHERE parent_id = #{parentId}")
    void deleteRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 更新戏曲评论数
     */
    @Update("UPDATE opera SET comment_count = (" +
            "SELECT COUNT(*) FROM comments WHERE opera_id = #{operaId} AND status = 1" +
            ") WHERE id = #{operaId}")
    void updateOperaCommentCount(@Param("operaId") Long operaId);

    /**
     * 检查用户是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM comment_likes " +
            "WHERE comment_id = #{commentId} AND user_id = #{userId}")
    boolean checkLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 添加点赞
     */
    @Insert("INSERT INTO comment_likes (comment_id, user_id, create_time) " +
            "VALUES (#{commentId}, #{userId}, NOW())")
    void insertLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 删除点赞
     */
    @Delete("DELETE FROM comment_likes WHERE comment_id = #{commentId} AND user_id = #{userId}")
    void deleteLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE comments SET like_count = like_count + 1, update_time = NOW() WHERE id = #{commentId}")
    void incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE comments SET like_count = GREATEST(like_count - 1, 0), update_time = NOW() WHERE id = #{commentId}")
    void decrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 检查用户角色
     */
    @Select("SELECT COUNT(*) > 0 FROM user_roles ur " +
            "JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.role_name = #{roleName}")
    boolean checkUserRole(@Param("userId") Long userId, @Param("roleName") String roleName);
}
