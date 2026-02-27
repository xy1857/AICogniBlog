package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章标签实体类
 * 
 * <p>对应数据库表 tag，存储文章标签信息
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("tag")
public class Tag {

    /** 标签 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 标签名称 */
    private String name;

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
}
