package com.aicogniblog.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章分类实体类
 * 
 * <p>对应数据库表 category，存储文章分类信息
 * 
 * @author AICogniBlog Team
 * @since 1.0.0
 */
@Data
@TableName("category")
public class Category {

    /** 分类 ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 分类名称 */
    private String name;

    /** 分类别名（用于 URL，唯一） */
    private String slug;

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
