package com.aicogniblog.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCommentVO {
    private Long id;
    private String content;
    private Integer status;
    private Integer aiAuditResult;
    private String aiReplySuggestion;
    private LocalDateTime createdAt;

    private UserBrief user;
    private ArticleBrief article;

    @Data
    public static class UserBrief {
        private Long id;
        private String nickname;
    }

    @Data
    public static class ArticleBrief {
        private Long id;
        private String title;
    }
}
