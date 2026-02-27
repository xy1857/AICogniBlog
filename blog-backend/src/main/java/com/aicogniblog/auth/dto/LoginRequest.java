package com.aicogniblog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求 DTO
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 1000, message = "密码加密数据过长")
    private String password;
}
