package com.aicogniblog.user.service;

import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.user.dto.FollowUserVO;

public interface FollowService {
    void follow(Long followerId, Long followingId);
    void unfollow(Long followerId, Long followingId);
    boolean isFollowing(Long followerId, Long followingId);
    PageResult<FollowUserVO> listFollowing(Long userId, int page, int size);
}
