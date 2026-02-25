package com.aicogniblog.ai.dto;

import lombok.Data;

@Data
public class AiAuditResult {
    private boolean safe;
    private String reason;
    private String replySuggestion;
}
