package com.aicogniblog.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户关注实体类
 * 
 * <p>对应数据库表 follow，存储用户之间的关注关系
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("follow")
public class Follow {

    /** 关注者用户 ID */
    private Long followerId;

    /** 被关注者用户 ID */
    private Long followingId;

    /** 关注时间 */
    private LocalDateTime createdAt;
}
