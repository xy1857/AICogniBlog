package com.aicogniblog.guestbook.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言板实体类
 */
@Data
public class Guestbook {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String content;
    private String avatarUrl;
    private String ipAddress;
    private Integer status;  // 0=待审核 1=已发布 2=已拒绝
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

