package com.aicogniblog.article.service;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;

public interface ArticleService {
    PageResult<ArticleVO> listArticles(int page, int size, Integer categoryId, Integer tagId, String keyword);
    PageResult<ArticleVO> listMyDrafts(Long userId, int page, int size);
    ArticleVO getArticleById(Long id);
    Long createArticle(ArticleRequest request, Long authorId);
    void updateArticle(Long id, ArticleRequest request);
    void deleteArticle(Long id);
    Long getArticleAuthorId(Long id);
    ArticleEditVO getArticleForEdit(Long id, Long userId, boolean isAdmin);
}
