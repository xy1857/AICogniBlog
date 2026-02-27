package com.aicogniblog.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * 
 * <p>对应数据库表 comment，存储评论内容和 AI 审核结果
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("comment")
public class Comment {

    /** 评论 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章 ID */
    private Long articleId;

    /** 评论用户 ID */
    private Long userId;

    /** 父评论 ID（用于嵌套评论，顶级评论为 NULL） */
    private Long parentId;

    /** 评论内容 */
    private String content;

    /**
     * 评论状态
     * <ul>
     *   <li>0 - 待审核</li>
     *   <li>1 - 已发布（审核通过）</li>
     *   <li>2 - 已拒绝（审核不通过）</li>
     * </ul>
     */
    private Integer status;

    /**
     * AI 审核结果
     * <ul>
     *   <li>0 - 不安全（包含违规内容）</li>
     *   <li>1 - 安全（内容合规）</li>
     * </ul>
     */
    private Integer aiAuditResult;

    /** AI 生成的回复建议 */
    private String aiReplySuggestion;

    /**
     * 逻辑删除标记
     * <ul>
     *   <li>0 - 未删除</li>
     *   <li>1 - 已删除</li>
     * </ul>
     */
    @TableLogic
    private Integer deleted;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
