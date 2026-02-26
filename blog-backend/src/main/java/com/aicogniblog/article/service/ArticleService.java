package com.aicogniblog.article.service;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;

import java.util.List;

public interface ArticleService {
    PageResult<ArticleVO> listArticles(int page, int size, Integer categoryId, String categorySlug, Integer tagId, String keyword);
    PageResult<ArticleVO> listArticlesBySubscription(List<Integer> categoryIds, List<Integer> tagIds, int page, int size);
    PageResult<ArticleVO> listMyDrafts(Long userId, int page, int size);
    ArticleVO getArticleById(Long id);
    ArticleVO getArticleById(Long id, Long userId);
    void likeArticle(Long articleId, Long userId);
    void unlikeArticle(Long articleId, Long userId);
    PageResult<ArticleVO> listLikedArticles(Long userId, int page, int size);
    void recordBrowse(Long articleId, Long userId);
    PageResult<ArticleVO> listFootprints(Long userId, int page, int size);
    Long createArticle(ArticleRequest request, Long authorId);
    void updateArticle(Long id, ArticleRequest request);
    void deleteArticle(Long id);
    Long getArticleAuthorId(Long id);
    ArticleEditVO getArticleForEdit(Long id, Long userId, boolean isAdmin);
}
