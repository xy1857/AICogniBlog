package com.aicogniblog.user.service.impl;

import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.user.dto.FollowUserVO;
import com.aicogniblog.user.entity.Follow;
import com.aicogniblog.user.mapper.FollowMapper;
import com.aicogniblog.user.service.FollowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BizException(400, "不能关注自己");
        }
        User target = userMapper.selectById(followingId);
        if (target == null || target.getStatus() == 0) {
            throw new BizException(404, "用户不存在或已禁用");
        }
        long exists = followMapper.selectCount(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, followerId)
                        .eq(Follow::getFollowingId, followingId));
        if (exists > 0) return;
        Follow f = new Follow();
        f.setFollowerId(followerId);
        f.setFollowingId(followingId);
        f.setCreatedAt(LocalDateTime.now());
        followMapper.insert(f);
    }

    @Override
    public void unfollow(Long followerId, Long followingId) {
        followMapper.delete(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, followerId)
                        .eq(Follow::getFollowingId, followingId));
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.selectCount(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, followerId)
                        .eq(Follow::getFollowingId, followingId)) > 0;
    }

    @Override
    public PageResult<FollowUserVO> listFollowing(Long userId, int page, int size) {
        Page<Follow> followPage = new Page<>(page, size);
        followMapper.selectPage(followPage,
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, userId)
                        .orderByDesc(Follow::getCreatedAt));
        List<Follow> list = followPage.getRecords();
        if (list.isEmpty()) return PageResult.of(new Page<>());
        List<Long> followingIds = list.stream().map(Follow::getFollowingId).distinct().toList();
        List<User> users = userMapper.selectBatchIds(followingIds);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        List<FollowUserVO> records = list.stream()
                .map(f -> {
                    FollowUserVO vo = new FollowUserVO();
                    vo.setFollowedAt(f.getCreatedAt());
                    User u = userMap.get(f.getFollowingId());
                    if (u != null) {
                        vo.setId(u.getId());
                        vo.setNickname(u.getNickname());
                        vo.setAvatarBase64(u.getAvatarBase64());
                    }
                    return vo;
                })
                .toList();
        PageResult<FollowUserVO> result = new PageResult<>();
        result.setTotal(followPage.getTotal());
        result.setCurrent((long) page);
        result.setPages(followPage.getPages());
        result.setRecords(records);
        return result;
    }
}
