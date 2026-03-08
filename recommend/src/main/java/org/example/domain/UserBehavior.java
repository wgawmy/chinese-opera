package org.example.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_behavior")
public class UserBehavior {
    private Long userId;
    private Long movieId;
    private Long rate;
    private LocalDateTime createTime;
}
