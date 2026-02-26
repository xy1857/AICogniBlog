package com.aicogniblog.user.service;

import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.user.dto.SubscriptionVO;

import java.util.List;

public interface SubscriptionService {
    void subscribe(Long userId, String targetType, Long targetId);
    void unsubscribe(Long userId, String targetType, Long targetId);
    List<SubscriptionVO> listMySubscriptions(Long userId);
    PageResult<ArticleVO> listSubscribedArticles(Long userId, int page, int size);
}
