package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("browse_history")
public class BrowseHistory {

    private Long userId;

    private Long articleId;

    private LocalDateTime viewedAt;
}
