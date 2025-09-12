package com.example.springboot.domain.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private Long userId;
    private Long operaId;
    private Long parentId;
}
