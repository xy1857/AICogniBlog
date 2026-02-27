package com.aicogniblog.auth.service.impl;

import com.aicogniblog.auth.dto.LoginRequest;
import com.aicogniblog.auth.dto.LoginResponse;
import com.aicogniblog.auth.dto.RegisterRequest;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.auth.security.JwtUtil;
import com.aicogniblog.auth.service.AuthService;
import com.aicogniblog.auth.service.LoginAttemptService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.util.PasswordValidator;
import com.aicogniblog.common.util.RsaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    @Value("${rsa.private-key}")
    private String privateKey;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * 解密密码
     * 
     * @param encryptedPassword RSA 加密的密码
     * @return 解密后的明文密码
     */
    private String decryptPassword(String encryptedPassword) {
        try {
            return RsaUtil.decrypt(encryptedPassword, privateKey);
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new BizException("密码解密失败，请检查密码格式");
        }
    }

    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return true 表示已存在
     */
    private boolean isUsernameExists(String username) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return count > 0;
    }

    /**
     * 检查邮箱是否已存在
     * 
     * @param email 邮箱
     * @return true 表示已存在
     */
    private boolean isEmailExists(String email) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        log.info("用户注册请求: username={}, email={}", request.getUsername(), request.getEmail());
        
        // 校验用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            log.warn("注册失败: 用户名已被占用 - {}", request.getUsername());
            throw new BizException("用户名已被占用");
        }

        // 校验邮箱是否已存在
        if (isEmailExists(request.getEmail())) {
            log.warn("注册失败: 邮箱已被注册 - {}", request.getEmail());
            throw new BizException("邮箱已被注册");
        }

        // 解密密码
        String decryptedPassword = decryptPassword(request.getPassword());
        
        // 验证密码格式
        String validationError = PasswordValidator.validate(decryptedPassword);
        if (validationError != null) {
            log.warn("注册失败: 密码格式不正确 - {}", validationError);
            throw new BizException(validationError);
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(decryptedPassword));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        log.info("用户登录请求: username={}", username);
        
        // 检查是否被锁定
        if (loginAttemptService.isBlocked(username)) {
            long remainingMinutes = loginAttemptService.getRemainingLockTime(username);
            log.warn("登录失败: 账号已被锁定 - username={}, 剩余时间={}分钟", username, remainingMinutes);
            throw new BizException(403, "登录失败次数过多，账号已被锁定 " + remainingMinutes + " 分钟");
        }
        
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if (user == null) {
            loginAttemptService.loginFailed(username);
            int remaining = loginAttemptService.getRemainingAttempts(username);
            log.warn("登录失败: 用户不存在 - username={}, 剩余尝试次数={}", username, remaining);
            throw new BizException(401, "用户名或密码错误" + (remaining > 0 ? "，剩余尝试次数: " + remaining : ""));
        }

        // 解密密码
        String decryptedPassword = decryptPassword(request.getPassword());

        // 验证密码
        if (!passwordEncoder.matches(decryptedPassword, user.getPasswordHash())) {
            loginAttemptService.loginFailed(username);
            int remaining = loginAttemptService.getRemainingAttempts(username);
            log.warn("登录失败: 密码错误 - username={}, 剩余尝试次数={}", username, remaining);
            throw new BizException(401, "用户名或密码错误" + (remaining > 0 ? "，剩余尝试次数: " + remaining : ""));
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            log.warn("登录失败: 账号已被禁用 - username={}", username);
            throw new BizException(403, "账号已被禁用，请联系管理员");
        }

        // 登录成功，清除失败记录
        loginAttemptService.loginSucceeded(username);

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        long expiresAt = System.currentTimeMillis() + jwtExpiration;
        
        log.info("用户登录成功: userId={}, username={}", user.getId(), username);
        
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarBase64(user.getAvatarBase64())
                .expiresAt(expiresAt)
                .build();
    }
}
