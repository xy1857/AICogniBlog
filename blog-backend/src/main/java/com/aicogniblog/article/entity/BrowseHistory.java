package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 浏览历史实体类
 * 
 * <p>对应数据库表 browse_history，存储用户的文章浏览记录
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("browse_history")
public class BrowseHistory {

    /** 用户 ID */
    private Long userId;

    /** 文章 ID */
    private Long articleId;

    /** 浏览时间 */
    private LocalDateTime viewedAt;
}
