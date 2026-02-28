package com.aicogniblog.stats.dto;

import lombok.Data;

/**
 * 统计数据 VO
 */
@Data
public class StatsVO {
    private Long totalViews;        // 总访问量
    private Long todayViews;        // 今日访问量
    private Long articleCount;      // 文章总数
    private Long commentCount;      // 评论总数
    private Integer visitorCountries; // 访客国家数（暂时固定值）
}

