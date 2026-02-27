package com.aicogniblog.article.controller;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文章管理控制器
 * 
 * <p>提供文章的增删改查、点赞、草稿管理、分类标签查询等功能接口
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    /**
     * 获取当前用户的草稿列表（分页）
     * 
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @param auth 当前登录用户认证信息
     * @return 草稿列表分页结果
     */
    @GetMapping("/api/articles/drafts")
    public Result<PageResult<ArticleVO>> listMyDrafts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(articleService.listMyDrafts(userId, page, size));
    }

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
     * @param keyword 搜索关键词（可选，搜索标题和摘要）
     * @return 文章列表分页结果
     */
    @GetMapping("/api/articles")
    public Result<PageResult<ArticleVO>> listArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.listArticles(page, size, categoryId, categorySlug, tagId, keyword));
    }

    /**
     * 获取文章详情
     * 
     * <p>返回文章完整内容，同时记录浏览历史（如果用户已登录）
     * 
     * @param id 文章 ID
     * @param auth 当前登录用户认证信息（可选）
     * @return 文章详情
     */
    @GetMapping("/api/articles/{id}")
    public Result<ArticleVO> getArticle(@PathVariable Long id, Authentication auth) {
        Long userId = auth != null && auth.getPrincipal() != null ? (Long) auth.getPrincipal() : null;
        return Result.success(articleService.getArticleById(id, userId));
    }

    /**
     * 点赞文章
     * 
     * @param id 文章 ID
     * @param auth 当前登录用户认证信息
     * @return 操作结果
     */
    @PostMapping("/api/articles/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        articleService.likeArticle(id, userId);
        return Result.success("已点赞");
    }

    /**
     * 取消点赞文章
     * 
     * @param id 文章 ID
     * @param auth 当前登录用户认证信息
     * @return 操作结果
     */
    @DeleteMapping("/api/articles/{id}/like")
    public Result<Void> unlikeArticle(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        articleService.unlikeArticle(id, userId);
        return Result.success("已取消");
    }

    /**
     * 获取文章编辑数据
     * 
     * <p>返回文章的 Markdown 原文和关联的标签，用于编辑页面
     * 
     * @param id 文章 ID
     * @param auth 当前登录用户认证信息
     * @return 文章编辑数据
     */
    @GetMapping("/api/articles/{id}/edit")
    public Result<ArticleEditVO> getArticleForEdit(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(articleService.getArticleForEdit(id, userId, isAdmin));
    }

    /**
     * 创建文章
     * 
     * <p>将 Markdown 内容渲染为 HTML，保存文章基本信息和标签关联
     * 
     * @param request 文章创建请求对象
     * @param auth 当前登录用户认证信息
     * @return 新创建的文章 ID
     */
    @PostMapping("/api/articles")
    public Result<Map<String, Long>> createArticle(@Valid @RequestBody ArticleRequest request,
                                                   Authentication auth) {
        Long authorId = (Long) auth.getPrincipal();
        Long id = articleService.createArticle(request, authorId);
        return Result.success("创建成功", Map.of("id", id));
    }

    /**
     * 更新文章
     * 
     * <p>只有文章作者或管理员可以编辑文章
     * 
     * @param id 文章 ID
     * @param request 文章更新请求对象
     * @param auth 当前登录用户认证信息
     * @return 操作结果
     * @throws BizException 如果用户无权限编辑该文章
     */
    @PutMapping("/api/articles/{id}")
    public Result<Void> updateArticle(@PathVariable Long id,
                                      @Valid @RequestBody ArticleRequest request,
                                      Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long authorId = articleService.getArticleAuthorId(id);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !userId.equals(authorId)) {
            throw new BizException(403, "无权限编辑该文章");
        }
        articleService.updateArticle(id, request);
        return Result.success("更新成功");
    }

    /**
     * 删除文章
     * 
     * <p>只有文章作者或管理员可以删除文章，使用逻辑删除
     * 
     * @param id 文章 ID
     * @param auth 当前登录用户认证信息
     * @return 操作结果
     * @throws BizException 如果用户无权限删除该文章
     */
    @DeleteMapping("/api/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Long authorId = articleService.getArticleAuthorId(id);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !userId.equals(authorId)) {
            throw new BizException(403, "无权限删除该文章");
        }
        articleService.deleteArticle(id);
        return Result.success("删除成功");
    }

    /**
     * 获取所有分类列表
     * 
     * <p>返回分类信息及每个分类下的文章数量
     * 
     * @return 分类列表
     */
    @GetMapping("/api/categories")
    public Result<List<Map<String, Object>>> listCategories() {
        return Result.success(categoryMapper.selectWithArticleCount());
    }

    /**
     * 获取所有标签列表
     * 
     * <p>返回标签信息及每个标签下的文章数量
     * 
     * @return 标签列表
     */
    @GetMapping("/api/tags")
    public Result<List<Map<String, Object>>> listTags() {
        return Result.success(tagMapper.selectWithArticleCount());
    }
}
