package com.aicogniblog.article.service;

import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;

public interface ArticleService {
    PageResult<ArticleVO> listArticles(int page, int size, Integer categoryId, Integer tagId, String keyword);
    ArticleVO getArticleById(Long id);
    Long createArticle(ArticleRequest request, Long authorId);
    void updateArticle(Long id, ArticleRequest request);
    void deleteArticle(Long id);
}
