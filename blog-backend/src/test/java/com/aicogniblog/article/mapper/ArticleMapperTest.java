package com.aicogniblog.article.mapper;

import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.entity.Category;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("文章Mapper测试")
class ArticleMapperTest {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private Long testUserId;
    private Integer testCategoryId;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("password");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        user.setRole(0);
        userMapper.insert(user);
        testUserId = user.getId();

        // 创建测试分类
        Category category = new Category();
        category.setName("技术");
        category.setSlug("tech");
        categoryMapper.insert(category);
        testCategoryId = category.getId();
    }

    @Test
    @DisplayName("插入文章 - 成功")
    void testInsertArticle() {
        // Given
        Article article = new Article();
        article.setAuthorId(testUserId);
        article.setCategoryId(testCategoryId);
        article.setTitle("测试文章");
        article.setSummary("摘要");
        article.setContentMd("# 内容");
        article.setContentHtml("<h1>内容</h1>");
        article.setStatus(1);
        article.setViewCount(0);
        article.setPublishedAt(LocalDateTime.now());

        // When
        int result = articleMapper.insert(article);

        // Then
        assertEquals(1, result);
        assertNotNull(article.getId());
    }

    @Test
    @DisplayName("根据ID查询文章 - 成功")
    void testSelectById() {
        // Given
        Article article = new Article();
        article.setAuthorId(testUserId);
        article.setTitle("测试文章");
        article.setContentMd("内容");
        article.setStatus(1);
        article.setViewCount(0);
        articleMapper.insert(article);

        // When
        Article found = articleMapper.selectById(article.getId());

        // Then
        assertNotNull(found);
        assertEquals("测试文章", found.getTitle());
        assertEquals(testUserId, found.getAuthorId());
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void testUpdateArticle() {
        // Given
        Article article = new Article();
        article.setAuthorId(testUserId);
        article.setTitle("原标题");
        article.setContentMd("原内容");
        article.setStatus(0);
        article.setViewCount(0);
        articleMapper.insert(article);

        // When
        article.setTitle("新标题");
        article.setStatus(1);
        int result = articleMapper.updateById(article);

        // Then
        assertEquals(1, result);
        Article updated = articleMapper.selectById(article.getId());
        assertEquals("新标题", updated.getTitle());
        assertEquals(1, updated.getStatus());
    }

    @Test
    @DisplayName("删除文章 - 逻辑删除")
    void testDeleteArticle() {
        // Given
        Article article = new Article();
        article.setAuthorId(testUserId);
        article.setTitle("待删除文章");
        article.setContentMd("内容");
        article.setStatus(1);
        article.setViewCount(0);
        articleMapper.insert(article);
        Long articleId = article.getId();

        // When
        int result = articleMapper.deleteById(articleId);

        // Then
        assertEquals(1, result);
        Article deleted = articleMapper.selectById(articleId);
        assertNull(deleted); // 逻辑删除后查询不到
    }

    @Test
    @DisplayName("条件查询 - 按状态查询")
    void testSelectByStatus() {
        // Given
        Article draft = new Article();
        draft.setAuthorId(testUserId);
        draft.setTitle("草稿");
        draft.setContentMd("内容");
        draft.setStatus(0);
        draft.setViewCount(0);
        articleMapper.insert(draft);

        Article published = new Article();
        published.setAuthorId(testUserId);
        published.setTitle("已发布");
        published.setContentMd("内容");
        published.setStatus(1);
        published.setViewCount(0);
        articleMapper.insert(published);

        // When
        List<Article> publishedArticles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));

        // Then
        assertEquals(1, publishedArticles.size());
        assertEquals("已发布", publishedArticles.get(0).getTitle());
    }

    @Test
    @DisplayName("条件查询 - 按作者查询")
    void testSelectByAuthor() {
        // Given
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setPasswordHash("password");
        anotherUser.setNickname("另一个用户");
        anotherUser.setRole(0);
        userMapper.insert(anotherUser);

        Article article1 = new Article();
        article1.setAuthorId(testUserId);
        article1.setTitle("文章1");
        article1.setContentMd("内容");
        article1.setStatus(1);
        article1.setViewCount(0);
        articleMapper.insert(article1);

        Article article2 = new Article();
        article2.setAuthorId(anotherUser.getId());
        article2.setTitle("文章2");
        article2.setContentMd("内容");
        article2.setStatus(1);
        article2.setViewCount(0);
        articleMapper.insert(article2);

        // When
        List<Article> myArticles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getAuthorId, testUserId));

        // Then
        assertEquals(1, myArticles.size());
        assertEquals("文章1", myArticles.get(0).getTitle());
    }

    @Test
    @DisplayName("条件查询 - 按分类查询")
    void testSelectByCategory() {
        // Given
        Category category2 = new Category();
        category2.setName("生活");
        category2.setSlug("life");
        categoryMapper.insert(category2);

        Article article1 = new Article();
        article1.setAuthorId(testUserId);
        article1.setCategoryId(testCategoryId);
        article1.setTitle("技术文章");
        article1.setContentMd("内容");
        article1.setStatus(1);
        article1.setViewCount(0);
        articleMapper.insert(article1);

        Article article2 = new Article();
        article2.setAuthorId(testUserId);
        article2.setCategoryId(category2.getId());
        article2.setTitle("生活文章");
        article2.setContentMd("内容");
        article2.setStatus(1);
        article2.setViewCount(0);
        articleMapper.insert(article2);

        // When
        List<Article> techArticles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, testCategoryId));

        // Then
        assertEquals(1, techArticles.size());
        assertEquals("技术文章", techArticles.get(0).getTitle());
    }

    @Test
    @DisplayName("模糊查询 - 按标题搜索")
    void testSelectByTitleLike() {
        // Given
        Article article1 = new Article();
        article1.setAuthorId(testUserId);
        article1.setTitle("Java编程指南");
        article1.setContentMd("内容");
        article1.setStatus(1);
        article1.setViewCount(0);
        articleMapper.insert(article1);

        Article article2 = new Article();
        article2.setAuthorId(testUserId);
        article2.setTitle("Python入门教程");
        article2.setContentMd("内容");
        article2.setStatus(1);
        article2.setViewCount(0);
        articleMapper.insert(article2);

        // When
        List<Article> javaArticles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().like(Article::getTitle, "Java"));

        // Then
        assertEquals(1, javaArticles.size());
        assertEquals("Java编程指南", javaArticles.get(0).getTitle());
    }

    @Test
    @DisplayName("统计查询 - 统计文章数量")
    void testCountArticles() {
        // Given
        for (int i = 1; i <= 5; i++) {
            Article article = new Article();
            article.setAuthorId(testUserId);
            article.setTitle("文章" + i);
            article.setContentMd("内容");
            article.setStatus(1);
            article.setViewCount(0);
            articleMapper.insert(article);
        }

        // When
        Long count = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));

        // Then
        assertEquals(5, count);
    }
}

