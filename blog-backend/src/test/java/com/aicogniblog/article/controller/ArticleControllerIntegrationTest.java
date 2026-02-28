package com.aicogniblog.article.controller;

import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.entity.Category;
import com.aicogniblog.article.entity.Tag;
import com.aicogniblog.article.mapper.ArticleMapper;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("文章控制器集成测试")
class ArticleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    private Long testUserId;
    private Integer testCategoryId;
    private Integer testTagId;


    @Test
    @DisplayName("GET /api/articles - 列出文章")
    void testListArticles() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setSummary("摘要");
        request.setContentMd("# 内容");
        request.setCategoryId(testCategoryId);
        request.setStatus(1);
        articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].title").value("测试文章"));
    }

    @Test
    @DisplayName("GET /api/articles - 按分类筛选")
    void testListArticlesByCategory() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("技术文章");
        request.setContentMd("内容");
        request.setCategoryId(testCategoryId);
        request.setStatus(1);
        articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles")
                        .param("categoryId", testCategoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].category.name").value("技术"));
    }

    @Test
    @DisplayName("GET /api/articles - 按关键词搜索")
    void testListArticlesByKeyword() throws Exception {
        // Given
        ArticleRequest request1 = new ArticleRequest();
        request1.setTitle("Java编程");
        request1.setContentMd("内容");
        request1.setStatus(1);
        articleService.createArticle(request1, testUserId);

        ArticleRequest request2 = new ArticleRequest();
        request2.setTitle("Python入门");
        request2.setContentMd("内容");
        request2.setStatus(1);
        articleService.createArticle(request2, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles")
                        .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].title").value("Java编程"));
    }

    @Test
    @DisplayName("GET /api/articles/{id} - 获取文章详情")
    void testGetArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setSummary("摘要");
        request.setContentMd("# 标题\n\n内容");
        request.setCategoryId(testCategoryId);
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles/{id}", articleId)
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试文章"))
                .andExpect(jsonPath("$.data.contentHtml").exists())
                .andExpect(jsonPath("$.data.author.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("GET /api/articles/{id} - 文章不存在")
    void testGetArticle_NotFound() throws Exception {
        mockMvc.perform(get("/api/articles/{id}", 99999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/articles - 创建文章")
    void testCreateArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("新文章");
        request.setSummary("摘要");
        request.setContentMd("# 内容");
        request.setCategoryId(testCategoryId);
        request.setTagIds(List.of(testTagId));
        request.setStatus(1);

        // When & Then
        mockMvc.perform(post("/api/articles")
                        .with(user(testUserId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    @DisplayName("POST /api/articles - 验证失败（标题为空）")
    void testCreateArticle_ValidationFailed() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("");  // 空标题
        request.setContentMd("内容");
        request.setStatus(1);

        // When & Then
        mockMvc.perform(post("/api/articles")
                        .with(user(testUserId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/articles/{id} - 更新文章")
    void testUpdateArticle() throws Exception {
        // Given
        ArticleRequest createRequest = new ArticleRequest();
        createRequest.setTitle("原标题");
        createRequest.setContentMd("原内容");
        createRequest.setStatus(0);
        Long articleId = articleService.createArticle(createRequest, testUserId);

        ArticleRequest updateRequest = new ArticleRequest();
        updateRequest.setTitle("新标题");
        updateRequest.setSummary("新摘要");
        updateRequest.setContentMd("# 新内容");
        updateRequest.setCategoryId(testCategoryId);
        updateRequest.setStatus(1);

        // When & Then
        mockMvc.perform(put("/api/articles/{id}", articleId)
                        .with(user(testUserId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));

        // 验证更新
        Article article = articleMapper.selectById(articleId);
        assert article != null;
        assert article.getTitle().equals("新标题");
    }

    @Test
    @DisplayName("DELETE /api/articles/{id} - 删除文章")
    void testDeleteArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("待删除文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(delete("/api/articles/{id}", articleId)
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证删除
        Article article = articleMapper.selectById(articleId);
        assert article == null;
    }

    @Test
    @DisplayName("POST /api/articles/{id}/like - 点赞文章")
    void testLikeArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(post("/api/articles/{id}/like", articleId)
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/articles/{id}/like - 取消点赞")
    void testUnlikeArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);
        articleService.likeArticle(articleId, testUserId);

        // When & Then
        mockMvc.perform(delete("/api/articles/{id}/like", articleId)
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/articles/drafts - 列出草稿")
    void testListMyDrafts() throws Exception {
        // Given
        ArticleRequest draft1 = new ArticleRequest();
        draft1.setTitle("草稿1");
        draft1.setContentMd("内容");
        draft1.setStatus(0);
        articleService.createArticle(draft1, testUserId);

        ArticleRequest draft2 = new ArticleRequest();
        draft2.setTitle("草稿2");
        draft2.setContentMd("内容");
        draft2.setStatus(0);
        articleService.createArticle(draft2, testUserId);

        ArticleRequest published = new ArticleRequest();
        published.setTitle("已发布");
        published.setContentMd("内容");
        published.setStatus(1);
        articleService.createArticle(published, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles/drafts")
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[*].title", containsInAnyOrder("草稿1", "草稿2")));
    }

    @Test
    @DisplayName("GET /api/articles/{id}/edit - 获取编辑数据")
    void testGetArticleForEdit() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setSummary("摘要");
        request.setContentMd("# 内容");
        request.setCategoryId(testCategoryId);
        request.setTagIds(List.of(testTagId));
        request.setStatus(0);
        Long articleId = articleService.createArticle(request, testUserId);

        // When & Then
        mockMvc.perform(get("/api/articles/{id}/edit", articleId)
                        .with(user(testUserId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("测试文章"))
                .andExpect(jsonPath("$.data.contentMd").value("# 内容"))
                .andExpect(jsonPath("$.data.categoryId").value(testCategoryId))
                .andExpect(jsonPath("$.data.tagIds[0]").value(testTagId));
    }

    @Test
    @DisplayName("GET /api/categories - 列出分类")
    void testListCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/tags - 列出标签")
    void testListTags() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("管理员可以编辑任何文章")
    void testAdminCanEditAnyArticle() throws Exception {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("用户文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        ArticleRequest updateRequest = new ArticleRequest();
        updateRequest.setTitle("管理员修改");
        updateRequest.setContentMd("新内容");
        updateRequest.setStatus(1);

        // When & Then
        mockMvc.perform(put("/api/articles/{id}", articleId)
                        .with(user("999").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}




