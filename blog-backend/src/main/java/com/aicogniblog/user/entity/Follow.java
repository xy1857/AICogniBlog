package com.aicogniblog.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("follow")
public class Follow {

    private Long followerId;

    private Long followingId;

    private LocalDateTime createdAt;
}
