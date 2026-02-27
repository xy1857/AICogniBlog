package com.aicogniblog.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * <p>对应数据库表 user，存储用户的基本信息、认证信息和权限信息
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("user")
public class User {

    /** 用户 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（登录账号，唯一） */
    private String username;

    /** 密码哈希值（BCrypt 加密） */
    private String passwordHash;

    /** 邮箱地址（唯一） */
    private String email;

    /** 昵称（显示名称） */
    private String nickname;

    /** Base64 编码的头像图片 */
    private String avatarBase64;

    /** 个人简介 */
    private String bio;

    /**
     * 用户角色
     * <ul>
     *   <li>0 - 普通用户</li>
     *   <li>1 - 管理员</li>
     * </ul>
     */
    private Integer role;

    /**
     * 账号状态
     * <ul>
     *   <li>0 - 禁用</li>
     *   <li>1 - 启用</li>
     * </ul>
     */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /**
     * 逻辑删除标记
     * <ul>
     *   <li>0 - 未删除</li>
     *   <li>1 - 已删除</li>
     * </ul>
     */
    @TableLogic
    private Integer deleted;

    /** 创建时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
