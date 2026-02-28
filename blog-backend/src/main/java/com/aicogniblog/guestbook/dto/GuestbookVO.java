package com.aicogniblog.guestbook.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言响应 DTO
 */
@Data
public class GuestbookVO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String content;
    private String avatarUrl;
    private LocalDateTime createdAt;
}

