package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long authorId;

    private Integer categoryId;

    private String title;

    private String summary;

    private String contentMd;

    private String contentHtml;

    private String coverUrl;

    /** 状态: 0=草稿 1=已发布 */
    private Integer status;

    private Integer viewCount;

    @TableLogic
    private Integer deleted;

    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
