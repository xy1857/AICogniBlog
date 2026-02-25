package com.aicogniblog.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long userId;

    private Long parentId;

    private String content;

    /** 状态: 0=待审核 1=已发布 2=已拒绝 */
    private Integer status;

    /** AI审核结果: 0=不安全 1=安全 */
    private Integer aiAuditResult;

    private String aiReplySuggestion;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
