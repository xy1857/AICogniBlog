package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_like")
public class ArticleLike {

    private Long userId;

    private Long articleId;

    private LocalDateTime createdAt;
}
