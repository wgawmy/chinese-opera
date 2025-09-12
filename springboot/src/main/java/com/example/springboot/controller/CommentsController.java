package com.example.springboot.controller;

import com.example.springboot.domain.Comment;
import com.example.springboot.domain.DTO.CommentDTO;
import com.example.springboot.service.CommentService;
import com.example.springboot.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentService commentService;

    /**
     * 根据戏曲ID获取评论列表
     * GET /comments/{operaId}
     *
     * 返回格式：
     * {
     *   "code": 200,
     *   "msg": "获取评论成功",
     *   "data": [
     *     {
     *       "id": 1,
     *       "content": "这个戏曲很棒！",
     *       "userId": 123,
     *       "userName": "戏曲爱好者",
     *       "userAvatar": "avatar.jpg",
     *       "operaId": 1,
     *       "parentId": null,
     *       "likeCount": 5,
     *       "createTime": "2025-09-12T10:30:00",
     *       "replies": []
     *     }
     *   ]
     * }
     */
    @GetMapping("/{operaId}")
    public ResponseResult<List<Comment>> getCommentsByOperaId(@PathVariable Long operaId) {
        try {
            List<Comment> comments = commentService.getCommentsByOperaId(operaId);
            return new ResponseResult<>(200, "获取评论成功", comments);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取评论失败：" + e.getMessage());
        }
    }

    /**
     * 添加评论
     * POST /comments/addcomment
     *
     * 请求体：
     * {
     *   "content": "评论内容",
     *   "userId": 123,
     *   "operaId": 1,
     *   "parentId": null  // 回复评论时传父评论ID
     * }
     *
     * 返回格式：
     * {
     *   "code": 200,
     *   "msg": "评论发布成功"
     * }
     */
    @PostMapping("/addcomment")
    public ResponseResult<String> addComment(@RequestBody CommentDTO commentDTO) {
        try {
            // 参数验证
            if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
                return new ResponseResult<>(400, "评论内容不能为空");
            }
            if (commentDTO.getUserId() == null) {
                return new ResponseResult<>(400, "用户ID不能为空");
            }
            if (commentDTO.getOperaId() == null) {
                return new ResponseResult<>(400, "戏曲ID不能为空");
            }

            commentService.addComment(commentDTO);
            return new ResponseResult<>(200, "评论发布成功");
        } catch (Exception e) {
            return new ResponseResult<>(500, "评论发布失败：" + e.getMessage());
        }
    }

    /**
     * 删除评论
     * DELETE /comments/{commentId}?userId={userId}
     *
     * 返回格式：
     * {
     *   "code": 200,
     *   "msg": "评论删除成功"
     * }
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult<String> deleteComment(@PathVariable Long commentId,
                                                @RequestParam Long userId) {
        try {
            // 权限验证
            boolean canDelete = commentService.canDeleteComment(commentId, userId);
            if (!canDelete) {
                return new ResponseResult<>(403, "无权限删除此评论");
            }

            commentService.deleteComment(commentId);
            return new ResponseResult<>(200, "评论删除成功");
        } catch (Exception e) {
            return new ResponseResult<>(500, "评论删除失败：" + e.getMessage());
        }
    }

    /**
     * 点赞/取消点赞评论
     * POST /comments/{commentId}/like?userId={userId}
     *
     * 返回格式：
     * {
     *   "code": 200,
     *   "msg": "操作成功",
     *   "data": {
     *     "action": "like",        // "like" 或 "unlike"
     *     "message": "点赞成功",
     *     "likeCount": 6,         // 最新点赞数
     *     "isLiked": true         // 当前用户是否已点赞
     *   }
     * }
     */
    @PostMapping("/{commentId}/like")
    public ResponseResult<Map<String, Object>> likeComment(@PathVariable Long commentId,
                                                           @RequestParam Long userId) {
        try {
            Map<String, Object> result = commentService.toggleLike(commentId, userId);
            return new ResponseResult<>(200, "操作成功", result);
        } catch (Exception e) {
            return new ResponseResult<>(500, "操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户对评论的点赞状态
     * GET /comments/{commentId}/like-status?userId={userId}
     *
     * 返回格式：
     * {
     *   "code": 200,
     *   "msg": "获取点赞状态成功",
     *   "data": true  // true表示已点赞，false表示未点赞
     * }
     */
    @GetMapping("/{commentId}/like-status")
    public ResponseResult<Boolean> getLikeStatus(@PathVariable Long commentId,
                                                 @RequestParam Long userId) {
        try {
            boolean isLiked = commentService.isLiked(commentId, userId);
            return new ResponseResult<>(200, "获取点赞状态成功", isLiked);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取点赞状态失败：" + e.getMessage());
        }
    }
}
