package com.aicogniblog.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 DTO
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@Builder
public class LoginResponse {
    /** JWT Token */
    private String token;
    
    /** 用户 ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 昵称 */
    private String nickname;
    
    /** 邮箱 */
    private String email;
    
    /** 用户角色：0-普通用户，1-管理员 */
    private Integer role;
    
    /** Base64 编码的头像 */
    private String avatarBase64;
    
    /** Token 过期时间（毫秒时间戳） */
    private Long expiresAt;
}
