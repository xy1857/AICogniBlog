package com.aicogniblog.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private String email;

    private String nickname;

    private String avatarBase64;

    private String bio;

    /** 角色: 0=普通用户 1=管理员 */
    private Integer role;

    /** 状态: 0=禁用 1=启用 */
    private Integer status;

    private LocalDateTime lastLoginAt;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
