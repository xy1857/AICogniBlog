package com.aicogniblog.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserInfo user;
    private List<CommentVO> replies;

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatarBase64;
    }
}
