package com.aicogniblog.auth.service.impl;

import com.aicogniblog.auth.dto.LoginRequest;
import com.aicogniblog.auth.dto.LoginResponse;
import com.aicogniblog.auth.dto.RegisterRequest;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.auth.security.JwtUtil;
import com.aicogniblog.auth.service.AuthService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.util.RsaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${rsa.private-key}")
    private String privateKey;

    @Override
    public void register(RegisterRequest request) {
        // 校验用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BizException("用户名已被占用");
        }

        // 校验邮箱是否已存在
        Long emailCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (emailCount > 0) {
            throw new BizException("邮箱已被注册");
        }

        // 解密密码
        String decryptedPassword;
        try {
            decryptedPassword = RsaUtil.decrypt(request.getPassword(), privateKey);
        } catch (Exception e) {
            throw new BizException("密码解密失败");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(decryptedPassword));
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername());
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));

        if (user == null) {
            throw new BizException(401, "用户名或密码错误");
        }

        // 解密密码
        String decryptedPassword;
        try {
            decryptedPassword = RsaUtil.decrypt(request.getPassword(), privateKey);
        } catch (Exception e) {
            throw new BizException("密码解密失败");
        }

        if (!passwordEncoder.matches(decryptedPassword, user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BizException(403, "账号已被禁用，请联系管理员");
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
