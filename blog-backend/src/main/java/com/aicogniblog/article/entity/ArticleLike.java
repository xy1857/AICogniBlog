package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章点赞实体类
 * 
 * <p>对应数据库表 article_like，存储用户对文章的点赞记录
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("article_like")
public class ArticleLike {

    /** 用户 ID */
    private Long userId;

    /** 文章 ID */
    private Long articleId;

    /** 点赞时间 */
    private LocalDateTime createdAt;
}
