package com.aicogniblog.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户订阅实体类
 * 
 * <p>对应数据库表 subscription，存储用户对分类或标签的订阅记录
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("subscription")
public class Subscription {

    /** 订阅 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID */
    private Long userId;

    /**
     * 订阅目标类型
     * <ul>
     *   <li>category - 分类</li>
     *   <li>tag - 标签</li>
     * </ul>
     */
    private String targetType;

    /** 订阅目标 ID（分类 ID 或标签 ID） */
    private Long targetId;

    /** 订阅时间 */
    private LocalDateTime createdAt;
}
