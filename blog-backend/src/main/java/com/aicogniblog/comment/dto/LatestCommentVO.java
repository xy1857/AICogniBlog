package com.aicogniblog.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LatestCommentVO {
    private Long id;
    private String content;
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
