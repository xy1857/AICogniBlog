package com.aicogniblog.stats.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 统计数据 Mapper
 */
@Mapper
public interface StatsMapper {
    
    @Select("SELECT COALESCE(SUM(view_count), 0) FROM article WHERE deleted = 0 AND status = 1")
    long getTotalViews();
    
    @Select("SELECT COUNT(*) FROM article WHERE deleted = 0 AND status = 1")
    long getArticleCount();
    
    @Select("SELECT COUNT(*) FROM comment WHERE deleted = 0 AND status = 1")
    long getCommentCount();
    
    // 今日访问量（简化实现，实际应该有专门的访问记录表）
    @Select("SELECT COUNT(*) FROM browse_history WHERE DATE(viewed_at) = CURDATE()")
    long getTodayViews();
}

