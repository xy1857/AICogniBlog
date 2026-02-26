package com.aicogniblog.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyCommentVO {
    private Long id;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private Long articleId;
    private String articleTitle;
}
