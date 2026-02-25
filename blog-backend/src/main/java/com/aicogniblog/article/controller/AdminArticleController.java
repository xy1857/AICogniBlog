package com.aicogniblog.article.controller;

import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @PostMapping
    public Result<Map<String, Long>> createArticle(@Valid @RequestBody ArticleRequest request,
                                                    Authentication auth) {
        Long authorId = (Long) auth.getPrincipal();
        Long id = articleService.createArticle(request, authorId);
        return Result.success("创建成功", Map.of("id", id));
    }

    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id,
                                       @Valid @RequestBody ArticleRequest request) {
        articleService.updateArticle(id, request);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("删除成功");
    }
}
