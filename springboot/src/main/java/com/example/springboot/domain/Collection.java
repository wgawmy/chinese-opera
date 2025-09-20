package com.example.springboot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("opera_collections")
public class Collection {
    @TableId(type = IdType.AUTO)
    private Integer collectionId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
