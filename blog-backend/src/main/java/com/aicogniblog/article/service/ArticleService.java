package com.aicogniblog.article.service;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.common.result.PageResult;

import java.util.List;

/**
 * 文章业务逻辑接口
 * 
 * <p>定义文章相关的核心业务方法，包括文章发布、编辑、查询、点赞、浏览记录等功能
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public interface ArticleService {
    
    /**
     * 获取文章列表（分页）
     * 
     * <p>支持按分类、标签、关键词筛选，返回已发布的文章
     * 
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @param categoryId 分类 ID（可选）
     * @param categorySlug 分类别名（可选）
     * @param tagId 标签 ID（可选）
     * @param keyword 搜索关键词（可选）
     * @return 文章列表分页结果
     */
    PageResult<ArticleVO> listArticles(int page, int size, Integer categoryId, String categorySlug, Integer tagId, String keyword);
    
    /**
     * 根据用户订阅获取文章列表（分页）
     * 
     * <p>返回用户订阅的分类和标签下的文章
     * 
     * @param categoryIds 订阅的分类 ID 列表
     * @param tagIds 订阅的标签 ID 列表
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @return 文章列表分页结果
     */
    PageResult<ArticleVO> listArticlesBySubscription(List<Integer> categoryIds, List<Integer> tagIds, int page, int size);
    
    /**
     * 获取用户的草稿列表（分页）
     * 
     * @param userId 用户 ID
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @return 草稿列表分页结果
     */
    PageResult<ArticleVO> listMyDrafts(Long userId, int page, int size);
    
    /**
     * 根据 ID 获取文章详情
     * 
     * @param id 文章 ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id);
    
    /**
     * 根据 ID 获取文章详情
     * 
     * <p>如果提供了用户 ID，会记录浏览历史并返回用户是否已点赞
     * 
     * @param id 文章 ID
     * @param userId 用户 ID（可选）
     * @return 文章详情
     */
    ArticleVO getArticleById(Long id, Long userId);
    
    /**
     * 点赞文章
     * 
     * @param articleId 文章 ID
     * @param userId 用户 ID
     */
    void likeArticle(Long articleId, Long userId);
    
    /**
     * 取消点赞文章
     * 
     * @param articleId 文章 ID
     * @param userId 用户 ID
     */
    void unlikeArticle(Long articleId, Long userId);
    
    /**
     * 获取用户点赞的文章列表（分页）
     * 
     * @param userId 用户 ID
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @return 点赞文章列表分页结果
     */
    PageResult<ArticleVO> listLikedArticles(Long userId, int page, int size);
    
    /**
     * 记录文章浏览历史
     * 
     * @param articleId 文章 ID
     * @param userId 用户 ID
     */
    void recordBrowse(Long articleId, Long userId);
    
    /**
     * 获取用户的浏览足迹（分页）
     * 
     * @param userId 用户 ID
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @return 浏览足迹列表分页结果
     */
    PageResult<ArticleVO> listFootprints(Long userId, int page, int size);
    
    /**
     * 创建文章
     * 
     * <p>将 Markdown 内容渲染为 HTML，保存文章基本信息和标签关联
     * 
     * @param request 文章创建请求对象
     * @param authorId 作者用户 ID
     * @return 新创建的文章 ID
     */
    Long createArticle(ArticleRequest request, Long authorId);
    
    /**
     * 更新文章
     * 
     * <p>更新文章内容和标签关联
     * 
     * @param id 文章 ID
     * @param request 文章更新请求对象
     */
    void updateArticle(Long id, ArticleRequest request);
    
    /**
     * 删除文章
     * 
     * <p>使用逻辑删除
     * 
     * @param id 文章 ID
     */
    void deleteArticle(Long id);
    
    /**
     * 获取文章作者 ID
     * 
     * @param id 文章 ID
     * @return 作者用户 ID
     */
    Long getArticleAuthorId(Long id);
    
    /**
     * 获取文章编辑数据
     * 
     * <p>返回文章的 Markdown 原文和关联的标签，用于编辑页面
     * 
     * @param id 文章 ID
     * @param userId 用户 ID
     * @param isAdmin 是否为管理员
     * @return 文章编辑数据
     */
    ArticleEditVO getArticleForEdit(Long id, Long userId, boolean isAdmin);
}
