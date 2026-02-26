package com.aicogniblog.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionVO {
    private Long id;
    private String targetType;
    private Long targetId;
    private String targetName;
    private LocalDateTime createdAt;
}
