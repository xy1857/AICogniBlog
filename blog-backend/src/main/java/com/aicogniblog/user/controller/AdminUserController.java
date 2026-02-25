package com.aicogniblog.user.controller;

import cn.hutool.core.util.RandomUtil;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import com.aicogniblog.user.dto.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<PageResult<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getNickname, keyword)
                   .or().like(User::getEmail, keyword);
        }

        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        Page<UserVO> voPage = (Page<UserVO>) userPage.convert(UserVO::from);
        return Result.success(PageResult.of(voPage));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) {
            throw new BizException("status 值无效，应为 0 或 1");
        }
        User user = userMapper.selectById(id);
        if (user == null) throw new BizException(404, "用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("操作成功");
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id,
                                   @RequestBody Map<String, Integer> body) {
        Integer role = body.get("role");
        if (role == null || (role != 0 && role != 1)) {
            throw new BizException("role 值无效，应为 0 或 1");
        }
        User user = userMapper.selectById(id);
        if (user == null) throw new BizException(404, "用户不存在");
        user.setRole(role);
        userMapper.updateById(user);
        return Result.success("角色修改成功");
    }

    @PutMapping("/{id}/reset-password")
    public Result<Map<String, String>> resetPassword(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BizException(404, "用户不存在");
        String newPassword = RandomUtil.randomString(8);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("密码已重置", Map.of("newPassword", newPassword));
    }
}
