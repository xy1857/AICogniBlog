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

import com.aicogniblog.comment.dto.LatestCommentVO;

import java.util.List;

/**
 * 评论管理控制器
 * 
 * <p>提供评论的发布、查询、审核等功能接口
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取最新评论列表
     * 
     * @param limit 返回数量限制（最大 100）
     * @return 最新评论列表
     */
    @GetMapping("/api/comments/latest")
    public Result<List<LatestCommentVO>> listLatest(@RequestParam(defaultValue = "20") int limit) {
        return Result.success(commentService.listLatest(Math.min(limit, 100)));
    }

    /**
     * 获取文章的评论列表
     * 
     * <p>返回已发布的评论，支持嵌套评论结构
     * 
     * @param articleId 文章 ID
     * @return 评论列表（树形结构）
     */
    @GetMapping("/api/articles/{articleId}/comments")
    public Result<List<CommentVO>> listComments(@PathVariable Long articleId) {
        return Result.success(commentService.listComments(articleId));
    }

    /**
     * 提交评论
     * 
     * <p>保存评论后自动触发 AI 异步审核
     * 
     * @param articleId 文章 ID
     * @param request 评论请求对象
     * @param auth 当前登录用户认证信息
     * @return 操作结果
     */
    @PostMapping("/api/articles/{articleId}/comments")
    public Result<Void> submitComment(@PathVariable Long articleId,
                                       @Valid @RequestBody CommentRequest request,
                                       Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        commentService.submitComment(articleId, request, userId);
        return Result.success("评论已提交，正在审核");
    }

    /**
     * 管理员获取评论列表（分页）
     * 
     * <p>支持按审核状态筛选
     * 
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @param status 审核状态（可选，0=待审核 1=已发布 2=已拒绝）
     * @return 评论列表分页结果
     */
    @GetMapping("/api/admin/comments")
    public Result<PageResult<AdminCommentVO>> adminListComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.adminListComments(page, size, status));
    }

    /**
     * 管理员通过评论
     * 
     * @param id 评论 ID
     * @return 操作结果
     */
    @PutMapping("/api/admin/comments/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        commentService.approve(id);
        return Result.success("已通过");
    }

    /**
     * 管理员拒绝评论
     * 
     * @param id 评论 ID
     * @return 操作结果
     */
    @PutMapping("/api/admin/comments/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        commentService.reject(id);
        return Result.success("已拒绝");
    }

    /**
     * 删除评论
     * 
     * <p>使用逻辑删除
     * 
     * @param id 评论 ID
     * @return 操作结果
     */
    @DeleteMapping("/api/admin/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success("删除成功");
    }
}
