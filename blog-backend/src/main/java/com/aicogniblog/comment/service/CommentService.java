package com.aicogniblog.comment.service;

import com.aicogniblog.comment.dto.AdminCommentVO;
import com.aicogniblog.comment.dto.CommentRequest;
import com.aicogniblog.comment.dto.CommentVO;
import com.aicogniblog.comment.dto.LatestCommentVO;
import com.aicogniblog.comment.dto.MyCommentVO;
import com.aicogniblog.common.result.PageResult;

import java.util.List;

/**
 * 评论业务逻辑接口
 * 
 * <p>提供评论的发布、查询、审核等功能，支持嵌套评论和 AI 智能审核
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public interface CommentService {
    
    /**
     * 获取文章的评论列表
     * 
     * <p>返回已发布的评论，支持嵌套评论结构
     * 
     * @param articleId 文章 ID
     * @return 评论列表（树形结构）
     */
    List<CommentVO> listComments(Long articleId);
    
    /**
     * 获取最新评论列表
     * 
     * @param limit 返回数量限制
     * @return 最新评论列表
     */
    List<LatestCommentVO> listLatest(int limit);
    
    /**
     * 获取用户的评论列表（分页）
     * 
     * @param userId 用户 ID
     * @param page 页码，从 1 开始
     * @param size 每页数量
     * @return 用户评论列表分页结果
     */
    PageResult<MyCommentVO> listMyComments(Long userId, int page, int size);
    
    /**
     * 提交评论
     * 
     * <p>保存评论后自动触发 AI 异步审核
     * 
     * @param articleId 文章 ID
     * @param request 评论请求对象
     * @param userId 用户 ID
     */
    void submitComment(Long articleId, CommentRequest request, Long userId);
    
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
    PageResult<AdminCommentVO> adminListComments(int page, int size, Integer status);
    
    /**
     * 管理员通过评论
     * 
     * @param id 评论 ID
     */
    void approve(Long id);
    
    /**
     * 管理员拒绝评论
     * 
     * @param id 评论 ID
     */
    void reject(Long id);
    
    /**
     * 删除评论
     * 
     * <p>使用逻辑删除
     * 
     * @param id 评论 ID
     */
    void deleteComment(Long id);
}
