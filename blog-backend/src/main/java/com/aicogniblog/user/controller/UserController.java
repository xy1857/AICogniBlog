package com.aicogniblog.user.controller;

import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.comment.dto.MyCommentVO;
import com.aicogniblog.comment.service.CommentService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import com.aicogniblog.common.util.RsaUtil;
import com.aicogniblog.user.dto.UpdatePasswordRequest;
import com.aicogniblog.user.dto.UpdateProfileRequest;
import com.aicogniblog.user.dto.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CommentService commentService;
    private final ArticleService articleService;

    @Value("${rsa.private-key}")
    private String privateKey;

    @GetMapping("/profile")
    public Result<UserVO> getProfile(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        return Result.success(UserVO.from(user));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                      Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarBase64() != null) user.setAvatarBase64(request.getAvatarBase64());
        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                       Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userMapper.selectById(userId);
        
        // 解密旧密码和新密码
        String decryptedOldPassword;
        String decryptedNewPassword;
        try {
            decryptedOldPassword = RsaUtil.decrypt(request.getOldPassword(), privateKey);
            decryptedNewPassword = RsaUtil.decrypt(request.getNewPassword(), privateKey);
        } catch (Exception e) {
            throw new BizException("密码解密失败");
        }
        
        if (!passwordEncoder.matches(decryptedOldPassword, user.getPasswordHash())) {
            throw new BizException("旧密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(decryptedNewPassword));
        userMapper.updateById(user);
        return Result.success("密码修改成功");
    }

    @GetMapping("/comments")
    public Result<PageResult<MyCommentVO>> listMyComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(commentService.listMyComments(userId, page, size));
    }

    @GetMapping("/likes")
    public Result<PageResult<ArticleVO>> listMyLikes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(articleService.listLikedArticles(userId, page, size));
    }

    @GetMapping("/footprints")
    public Result<PageResult<ArticleVO>> listMyFootprints(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(articleService.listFootprints(userId, page, size));
    }
}
