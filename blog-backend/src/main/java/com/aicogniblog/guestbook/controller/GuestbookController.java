package com.aicogniblog.guestbook.controller;

import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import com.aicogniblog.guestbook.dto.GuestbookRequest;
import com.aicogniblog.guestbook.dto.GuestbookVO;
import com.aicogniblog.guestbook.service.GuestbookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 留言板控制器
 */
@RestController
@RequestMapping("/api/guestbook")
@RequiredArgsConstructor
public class GuestbookController {
    
    private final GuestbookService guestbookService;
    
    /**
     * 获取留言列表
     */
    @GetMapping
    public Result<PageResult<GuestbookVO>> listMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(guestbookService.listMessages(page, size));
    }
    
    /**
     * 发表留言
     */
    @PostMapping
    public Result<Map<String, Long>> createMessage(
            @Valid @RequestBody GuestbookRequest request,
            Authentication auth,
            HttpServletRequest httpRequest) {
        
        Long userId = null;
        if (auth != null && auth.getPrincipal() != null) {
            userId = (Long) auth.getPrincipal();
        }
        
        String ipAddress = getClientIp(httpRequest);
        Long id = guestbookService.createMessage(request, userId, ipAddress);
        
        return Result.success("留言成功", Map.of("id", id));
    }
    
    /**
     * 删除留言（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id, Authentication auth) {
        // 只有管理员可以删除
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        
        if (!isAdmin) {
            return Result.error(403, "无权限删除留言");
        }
        
        guestbookService.deleteMessage(id);
        return Result.success("删除成功");
    }
    
    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

