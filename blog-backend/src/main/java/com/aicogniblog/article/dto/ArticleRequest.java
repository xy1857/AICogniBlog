package com.aicogniblog.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多200个字符")
    private String title;

    private String summary;

    @NotBlank(message = "内容不能为空")
    private String contentMd;

    private String coverUrl;

    private Integer categoryId;

    private List<Integer> tagIds;

    /** 0=草稿 1=发布 */
    private Integer status = 0;
}
