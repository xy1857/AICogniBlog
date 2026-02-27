package com.aicogniblog.auth.service;

import com.aicogniblog.auth.dto.LoginRequest;
import com.aicogniblog.auth.dto.LoginResponse;
import com.aicogniblog.auth.dto.RegisterRequest;

/**
 * 认证服务接口
 * 
 * <p>提供用户注册、登录等认证相关功能
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户注册
     * 
     * <p>创建新用户账号，密码使用 BCrypt 加密存储
     * 
     * @param request 注册请求对象
     * @throws com.aicogniblog.common.exception.BizException 如果用户名或邮箱已存在
     */
    void register(RegisterRequest request);
    
    /**
     * 用户登录
     * 
     * <p>验证用户名和密码，生成 JWT token
     * 
     * @param request 登录请求对象
     * @return 登录响应（包含 token 和用户信息）
     * @throws com.aicogniblog.common.exception.BizException 如果用户名或密码错误
     */
    LoginResponse login(LoginRequest request);
}
