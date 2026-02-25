package com.aicogniblog.article.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleEditVO {
    private Long id;
    private String title;
    private String summary;
    private String contentMd;
    private String coverUrl;
    private Integer categoryId;
    private List<Integer> tagIds;
    private Integer status;
}
