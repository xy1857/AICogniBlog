package com.aicogniblog.ai.service.impl;

import com.aicogniblog.ai.dto.AiAuditResult;
import com.aicogniblog.ai.service.AiService;
import com.aicogniblog.comment.entity.Comment;
import com.aicogniblog.comment.mapper.CommentMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.api-url}")
    private String apiUrl;

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.model}")
    private String model;

    @Override
    @Async("aiTaskExecutor")
    public void auditCommentAsync(Long commentId, String content, String articleTitle) {
        try {
            AiAuditResult result = callAiApi(content, articleTitle);
            Comment comment = commentMapper.selectById(commentId);
            if (comment == null) return;

            comment.setAiAuditResult(result.isSafe() ? 1 : 0);
            comment.setAiReplySuggestion(result.getReplySuggestion());
            // AI 判断安全则自动发布，否则保持待审核状态由管理员处理
            if (result.isSafe()) {
                comment.setStatus(1);
            }
            commentMapper.updateById(comment);
            log.info("AI 审核评论 [{}] 完成: safe={}", commentId, result.isSafe());
        } catch (Exception e) {
            log.error("AI 审核评论 [{}] 失败", commentId, e);
        }
    }

    @Override
    public AiAuditResult auditComment(Long commentId, String content, String articleTitle) {
        try {
            AiAuditResult result = callAiApi(content, articleTitle);
            Comment comment = commentMapper.selectById(commentId);
            if (comment != null) {
                comment.setAiAuditResult(result.isSafe() ? 1 : 0);
                comment.setAiReplySuggestion(result.getReplySuggestion());
                if (result.isSafe()) comment.setStatus(1);
                commentMapper.updateById(comment);
            }
            return result;
        } catch (Exception e) {
            log.error("AI 审核失败", e);
            AiAuditResult fallback = new AiAuditResult();
            fallback.setSafe(false);
            fallback.setReason("AI 服务暂不可用");
            fallback.setReplySuggestion("");
            return fallback;
        }
    }

    private AiAuditResult callAiApi(String content, String articleTitle) throws Exception {
        String prompt = """
                你是一个博客评论审核助手。请审核以下评论是否合规，并给出回复建议。
                
                文章标题：%s
                评论内容：%s
                
                请严格按照以下 JSON 格式返回（不要包含任何其他内容）：
                {"safe": true或false, "reason": "审核说明", "replySuggestion": "友好的回复建议"}
                
                审核标准：评论中不能含有辱骂、广告、违法、色情等内容。
                """.formatted(articleTitle, content);

        java.util.Map<String, Object> requestMap = new java.util.LinkedHashMap<>();
        requestMap.put("model", model);
        requestMap.put("messages", java.util.List.of(
                java.util.Map.of("role", "user", "content", prompt)
        ));
        requestMap.put("temperature", 0.3);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());
        String aiContent = root.path("choices").get(0).path("message").path("content").asText();

        // 提取 JSON 部分（防止模型在 JSON 前后输出多余文字）
        int start = aiContent.indexOf('{');
        int end = aiContent.lastIndexOf('}');
        if (start == -1 || end == -1) {
            throw new RuntimeException("AI 返回格式异常: " + aiContent);
        }
        String jsonStr = aiContent.substring(start, end + 1);
        return objectMapper.readValue(jsonStr, AiAuditResult.class);
    }
}
