package com.aicogniblog.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowUserVO {
    private Long id;
    private String nickname;
    private String avatarBase64;
    private LocalDateTime followedAt;
}
