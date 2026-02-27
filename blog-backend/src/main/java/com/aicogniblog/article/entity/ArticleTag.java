package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章标签关联实体类
 * 
 * <p>对应数据库表 article_tag，存储文章与标签的多对多关联关系
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_tag")
public class ArticleTag {
    
    /** 文章 ID */
    private Long articleId;
    
    /** 标签 ID */
    private Integer tagId;
}
