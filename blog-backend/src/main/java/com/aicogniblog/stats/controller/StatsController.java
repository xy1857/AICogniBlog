package com.aicogniblog.stats.controller;

import com.aicogniblog.common.result.Result;
import com.aicogniblog.stats.dto.StatsVO;
import com.aicogniblog.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    
    private final StatsService statsService;
    
    /**
     * 获取统计数据
     */
    @GetMapping
    public Result<StatsVO> getStats() {
        return Result.success(statsService.getStats());
    }
}

