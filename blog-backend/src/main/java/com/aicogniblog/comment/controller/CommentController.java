package com.aicogniblog.comment.controller;

import com.aicogniblog.comment.dto.AdminCommentVO;
import com.aicogniblog.comment.dto.CommentRequest;
import com.aicogniblog.comment.dto.CommentVO;
import com.aicogniblog.comment.service.CommentService;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/articles/{articleId}/comments")
    public Result<List<CommentVO>> listComments(@PathVariable Long articleId) {
        return Result.success(commentService.listComments(articleId));
    }

    @PostMapping("/api/articles/{articleId}/comments")
    public Result<Void> submitComment(@PathVariable Long articleId,
                                       @Valid @RequestBody CommentRequest request,
                                       Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commentService.submitComment(articleId, request, userId);
        return Result.success("评论已提交，正在审核");
    }

    @GetMapping("/api/admin/comments")
    public Result<PageResult<AdminCommentVO>> adminListComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.adminListComments(page, size, status));
    }

    @PutMapping("/api/admin/comments/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        commentService.approve(id);
        return Result.success("已通过");
    }

    @PutMapping("/api/admin/comments/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        commentService.reject(id);
        return Result.success("已拒绝");
    }

    @DeleteMapping("/api/admin/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("删除成功");
    }
}
