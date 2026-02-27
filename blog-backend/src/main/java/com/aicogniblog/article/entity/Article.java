package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * 
 * <p>对应数据库表 article，存储文章的基本信息、内容和统计数据
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("article")
public class Article {

    /** 文章 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 作者用户 ID */
    private Long authorId;

    /** 分类 ID */
    private Integer categoryId;

    /** 文章标题 */
    private String title;

    /** 文章摘要 */
    private String summary;

    /** Markdown 格式的文章内容 */
    private String contentMd;

    /** HTML 格式的文章内容（由 Markdown 渲染生成） */
    private String contentHtml;

    /** 封面图片 URL */
    private String coverUrl;

    /**
     * 文章状态
     * <ul>
     *   <li>0 - 草稿（未发布）</li>
     *   <li>1 - 已发布</li>
     * </ul>
     */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /**
     * 逻辑删除标记
     * <ul>
     *   <li>0 - 未删除</li>
     *   <li>1 - 已删除</li>
     * </ul>
     */
    @TableLogic
    private Integer deleted;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
