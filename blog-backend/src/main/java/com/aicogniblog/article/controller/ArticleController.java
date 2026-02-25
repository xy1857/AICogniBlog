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

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    @GetMapping("/api/articles")
    public Result<PageResult<ArticleVO>> listArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.listArticles(page, size, categoryId, tagId, keyword));
    }

    @GetMapping("/api/articles/{id}")
    public Result<ArticleVO> getArticle(@PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    @GetMapping("/api/articles/{id}/edit")
    public Result<ArticleEditVO> getArticleForEdit(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return Result.success(articleService.getArticleForEdit(id, userId, isAdmin));
    }

    @PostMapping("/api/articles")
    public Result<Map<String, Long>> createArticle(@Valid @RequestBody ArticleRequest request,
                                                   Authentication auth) {
        Long authorId = (Long) auth.getPrincipal();
        Long id = articleService.createArticle(request, authorId);
        return Result.success("创建成功", Map.of("id", id));
    }

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

    @GetMapping("/api/categories")
    public Result<List<Map<String, Object>>> listCategories() {
        return Result.success(categoryMapper.selectWithArticleCount());
    }

    @GetMapping("/api/tags")
    public Result<List<Map<String, Object>>> listTags() {
        return Result.success(tagMapper.selectWithArticleCount());
    }
}
