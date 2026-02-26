package com.aicogniblog.user.service.impl;

import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.entity.Category;
import com.aicogniblog.article.entity.Tag;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.user.dto.SubscriptionVO;
import com.aicogniblog.user.entity.Subscription;
import com.aicogniblog.user.mapper.SubscriptionMapper;
import com.aicogniblog.user.service.SubscriptionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleService articleService;

    @Override
    public void subscribe(Long userId, String targetType, Long targetId) {
        if (!"category".equals(targetType) && !"tag".equals(targetType)) {
            throw new BizException(400, "targetType 必须为 category 或 tag");
        }
        long exists = subscriptionMapper.selectCount(
                new LambdaQueryWrapper<Subscription>()
                        .eq(Subscription::getUserId, userId)
                        .eq(Subscription::getTargetType, targetType)
                        .eq(Subscription::getTargetId, targetId));
        if (exists > 0) return;
        Subscription sub = new Subscription();
        sub.setUserId(userId);
        sub.setTargetType(targetType);
        sub.setTargetId(targetId);
        subscriptionMapper.insert(sub);
    }

    @Override
    public void unsubscribe(Long userId, String targetType, Long targetId) {
        subscriptionMapper.delete(
                new LambdaQueryWrapper<Subscription>()
                        .eq(Subscription::getUserId, userId)
                        .eq(Subscription::getTargetType, targetType)
                        .eq(Subscription::getTargetId, targetId));
    }

    @Override
    public List<SubscriptionVO> listMySubscriptions(Long userId) {
        List<Subscription> list = subscriptionMapper.selectList(
                new LambdaQueryWrapper<Subscription>()
                        .eq(Subscription::getUserId, userId)
                        .orderByDesc(Subscription::getCreatedAt));
        List<SubscriptionVO> result = new ArrayList<>();
        List<Integer> categoryIds = list.stream().filter(s -> "category".equals(s.getTargetType())).map(s -> s.getTargetId().intValue()).distinct().toList();
        List<Integer> tagIds = list.stream().filter(s -> "tag".equals(s.getTargetType())).map(s -> s.getTargetId().intValue()).distinct().toList();
        Map<Integer, Category> categoryMap = categoryIds.isEmpty() ? Map.of() : categoryMapper.selectBatchIds(categoryIds).stream().collect(Collectors.toMap(Category::getId, c -> c));
        Map<Integer, Tag> tagMap = tagIds.isEmpty() ? Map.of() : tagMapper.selectBatchIds(tagIds).stream().collect(Collectors.toMap(Tag::getId, t -> t));
        for (Subscription s : list) {
            SubscriptionVO vo = new SubscriptionVO();
            vo.setId(s.getId());
            vo.setTargetType(s.getTargetType());
            vo.setTargetId(s.getTargetId());
            vo.setCreatedAt(s.getCreatedAt());
            if ("category".equals(s.getTargetType())) {
                Category c = categoryMap.get(s.getTargetId().intValue());
                vo.setTargetName(c != null ? c.getName() : null);
            } else {
                Tag t = tagMap.get(s.getTargetId().intValue());
                vo.setTargetName(t != null ? t.getName() : null);
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public PageResult<ArticleVO> listSubscribedArticles(Long userId, int page, int size) {
        List<Subscription> list = subscriptionMapper.selectList(
                new LambdaQueryWrapper<Subscription>().eq(Subscription::getUserId, userId));
        List<Integer> categoryIds = list.stream().filter(s -> "category".equals(s.getTargetType())).map(s -> s.getTargetId().intValue()).distinct().toList();
        List<Integer> tagIds = list.stream().filter(s -> "tag".equals(s.getTargetType())).map(s -> s.getTargetId().intValue()).distinct().toList();
        return articleService.listArticlesBySubscription(categoryIds, tagIds, page, size);
    }
}
