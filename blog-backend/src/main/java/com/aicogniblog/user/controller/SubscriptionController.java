package com.aicogniblog.user.controller;

import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import com.aicogniblog.user.dto.SubscriptionVO;
import com.aicogniblog.user.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public Result<Void> subscribe(@RequestBody SubscribeRequest request, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        subscriptionService.subscribe(userId, request.getTargetType(), request.getTargetId());
        return Result.success("订阅成功");
    }

    @DeleteMapping
    public Result<Void> unsubscribe(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        subscriptionService.unsubscribe(userId, targetType, targetId);
        return Result.success("已取消订阅");
    }

    @GetMapping
    public Result<List<SubscriptionVO>> listMy(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(subscriptionService.listMySubscriptions(userId));
    }

    @GetMapping("/articles")
    public Result<PageResult<ArticleVO>> listSubscribedArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(subscriptionService.listSubscribedArticles(userId, page, size));
    }

    @lombok.Data
    public static class SubscribeRequest {
        private String targetType;
        private Long targetId;
    }
}
