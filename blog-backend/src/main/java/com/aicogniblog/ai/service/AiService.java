package com.aicogniblog.ai.service;

import com.aicogniblog.ai.dto.AiAuditResult;

/**
 * AI 服务接口
 * 
 * <p>提供评论内容的智能审核功能，集成 DeepSeek AI 接口
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public interface AiService {
    
    /**
     * 异步审核评论内容
     * 
     * <p>调用 DeepSeek AI 接口判断评论是否违规，自动更新审核状态和生成回复建议
     * 
     * @param commentId 评论 ID
     * @param content 评论内容
     * @param articleTitle 文章标题（用于提供上下文）
     */
    void auditCommentAsync(Long commentId, String content, String articleTitle);
    
    /**
     * 同步审核评论内容
     * 
     * <p>调用 DeepSeek AI 接口判断评论是否违规，返回审核结果
     * 
     * @param commentId 评论 ID
     * @param content 评论内容
     * @param articleTitle 文章标题（用于提供上下文）
     * @return AI 审核结果
     */
    AiAuditResult auditComment(Long commentId, String content, String articleTitle);
}
