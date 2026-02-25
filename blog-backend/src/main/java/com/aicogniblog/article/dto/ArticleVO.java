package com.aicogniblog.article.dto;

import com.aicogniblog.article.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String contentHtml;
    private String coverUrl;
    private Integer status;
    private int viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    // 关联信息
    private AuthorInfo author;
    private CategoryInfo category;
    private List<TagInfo> tags;
    private long commentCount;

    @Data
    public static class AuthorInfo {
        private Long id;
        private String nickname;
        private String avatarBase64;
    }

    @Data
    public static class CategoryInfo {
        private Integer id;
        private String name;
        private String slug;
    }

    @Data
    public static class TagInfo {
        private Integer id;
        private String name;

        public static TagInfo from(Tag tag) {
            TagInfo info = new TagInfo();
            info.setId(tag.getId());
            info.setName(tag.getName());
            return info;
        }
    }
}
