package com.aicogniblog.article.controller;

import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/api/categories")
    public Result<List<Map<String, Object>>> listCategories() {
        return Result.success(categoryMapper.selectWithArticleCount());
    }

    @GetMapping("/api/tags")
    public Result<List<Map<String, Object>>> listTags() {
        return Result.success(tagMapper.selectWithArticleCount());
    }
}
