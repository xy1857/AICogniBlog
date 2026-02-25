package com.aicogniblog.ai.service;

import com.aicogniblog.ai.dto.AiAuditResult;

public interface AiService {
    void auditCommentAsync(Long commentId, String content, String articleTitle);
    AiAuditResult auditComment(Long commentId, String content, String articleTitle);
}
