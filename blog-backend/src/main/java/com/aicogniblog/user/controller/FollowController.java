package com.aicogniblog.user.controller;

import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import com.aicogniblog.user.dto.FollowUserVO;
import com.aicogniblog.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public Result<Void> follow(@PathVariable Long userId, Authentication auth) {
        Long followerId = (Long) auth.getPrincipal();
        followService.follow(followerId, userId);
        return Result.success("关注成功");
    }

    @DeleteMapping("/{userId}")
    public Result<Void> unfollow(@PathVariable Long userId, Authentication auth) {
        Long followerId = (Long) auth.getPrincipal();
        followService.unfollow(followerId, userId);
        return Result.success("已取消关注");
    }

    @GetMapping("/following")
    public Result<PageResult<FollowUserVO>> listFollowing(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(followService.listFollowing(userId, page, size));
    }

    @GetMapping("/check/{userId}")
    public Result<Boolean> checkFollowing(@PathVariable Long userId, Authentication auth) {
        Long followerId = (Long) auth.getPrincipal();
        return Result.success(followService.isFollowing(followerId, userId));
    }
}
