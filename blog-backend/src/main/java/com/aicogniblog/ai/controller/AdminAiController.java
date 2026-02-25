package com.aicogniblog.ai.controller;

import com.aicogniblog.ai.dto.AiAuditResult;
import com.aicogniblog.ai.service.AiService;
import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.mapper.ArticleMapper;
import com.aicogniblog.comment.entity.Comment;
import com.aicogniblog.comment.mapper.CommentMapper;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminAiController {

    private final AiService aiService;
    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    @PostMapping("/{id}/ai-review")
    public Result<AiAuditResult> aiReview(@PathVariable Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) throw new BizException(404, "评论不存在");

        Article article = articleMapper.selectById(comment.getArticleId());
        String articleTitle = article != null ? article.getTitle() : "";

        AiAuditResult result = aiService.auditComment(id, comment.getContent(), articleTitle);
        return Result.success("AI 审核完成", result);
    }
}
