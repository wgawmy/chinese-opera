package com.example.springboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private Long userId;
    private String userName;      // 关联查询用户名
    private String userAvatar;    // 关联查询用户头像
    private Long operaId;
    private Long parentId;
    private Integer likeCount;
    private Integer status;       // 1-正常，0-删除，2-审核中
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Comment> replies; // 子评论列表
}

