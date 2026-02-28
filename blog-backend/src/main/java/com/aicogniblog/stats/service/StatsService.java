package com.aicogniblog.stats.service;

import com.aicogniblog.stats.dto.StatsVO;
import com.aicogniblog.stats.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 统计数据服务
 */
@Service
@RequiredArgsConstructor
public class StatsService {
    
    private final StatsMapper statsMapper;
    
    /**
     * 获取统计数据
     */
    public StatsVO getStats() {
        StatsVO stats = new StatsVO();
        stats.setTotalViews(statsMapper.getTotalViews());
        stats.setTodayViews(statsMapper.getTodayViews());
        stats.setArticleCount(statsMapper.getArticleCount());
        stats.setCommentCount(statsMapper.getCommentCount());
        stats.setVisitorCountries(23); // 暂时固定值，实际需要根据 IP 统计
        
        return stats;
    }
}

