package com.aicogniblog.article.service;

import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.entity.Category;
import com.aicogniblog.article.entity.Tag;
import com.aicogniblog.article.mapper.*;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.comment.mapper.CommentMapper;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("文章服务单元测试")
class ArticleServiceTest {

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

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    private Long testUserId;
    private Integer testCategoryId;
    private Integer testTagId;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        user.setRole("USER");
        userMapper.insert(user);
        testUserId = user.getId();

        // 创建测试分类
        Category category = new Category();
        category.setName("技术");
        category.setSlug("tech");
        category.setDescription("技术文章");
        categoryMapper.insert(category);
        testCategoryId = category.getId();

        // 创建测试标签
        Tag tag = new Tag();
        tag.setName("Java");
        tagMapper.insert(tag);
        testTagId = tag.getId();
    }

    @Test
    @DisplayName("创建文章 - 成功")
    void testCreateArticle_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setSummary("这是一篇测试文章");
        request.setContentMd("# 标题\n\n这是内容");
        request.setCategoryId(testCategoryId);
        request.setTagIds(List.of(testTagId));
        request.setStatus(1);

        // When
        Long articleId = articleService.createArticle(request, testUserId);

        // Then
        assertNotNull(articleId);
        Article article = articleMapper.selectById(articleId);
        assertNotNull(article);
        assertEquals("测试文章", article.getTitle());
        assertEquals(testUserId, article.getAuthorId());
        assertEquals(1, article.getStatus());
        assertNotNull(article.getPublishedAt());
    }

    @Test
    @DisplayName("创建草稿文章 - 成功")
    void testCreateDraftArticle_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("草稿文章");
        request.setSummary("这是一篇草稿");
        request.setContentMd("# 草稿内容");
        request.setCategoryId(testCategoryId);
        request.setStatus(0);

        // When
        Long articleId = articleService.createArticle(request, testUserId);

        // Then
        Article article = articleMapper.selectById(articleId);
        assertEquals(0, article.getStatus());
        assertNull(article.getPublishedAt());
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void testUpdateArticle_Success() {
        // Given
        ArticleRequest createRequest = new ArticleRequest();
        createRequest.setTitle("原标题");
        createRequest.setSummary("原摘要");
        createRequest.setContentMd("原内容");
        createRequest.setCategoryId(testCategoryId);
        createRequest.setStatus(0);
        Long articleId = articleService.createArticle(createRequest, testUserId);

        ArticleRequest updateRequest = new ArticleRequest();
        updateRequest.setTitle("新标题");
        updateRequest.setSummary("新摘要");
        updateRequest.setContentMd("# 新内容");
        updateRequest.setCategoryId(testCategoryId);
        updateRequest.setStatus(1);

        // When
        articleService.updateArticle(articleId, updateRequest);

        // Then
        Article article = articleMapper.selectById(articleId);
        assertEquals("新标题", article.getTitle());
        assertEquals("新摘要", article.getSummary());
        assertEquals(1, article.getStatus());
        assertNotNull(article.getPublishedAt());
    }

    @Test
    @DisplayName("删除文章 - 成功")
    void testDeleteArticle_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("待删除文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When
        articleService.deleteArticle(articleId);

        // Then
        Article article = articleMapper.selectById(articleId);
        assertNull(article);
    }

    @Test
    @DisplayName("删除不存在的文章 - 抛出异常")
    void testDeleteArticle_NotFound() {
        // When & Then
        assertThrows(BizException.class, () -> {
            articleService.deleteArticle(99999L);
        });
    }

    @Test
    @DisplayName("获取文章详情 - 成功")
    void testGetArticleById_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setSummary("摘要");
        request.setContentMd("# 内容");
        request.setCategoryId(testCategoryId);
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When
        ArticleVO vo = articleService.getArticleById(articleId, testUserId);

        // Then
        assertNotNull(vo);
        assertEquals("测试文章", vo.getTitle());
        assertNotNull(vo.getContentHtml());
        assertNotNull(vo.getAuthor());
        assertEquals("测试用户", vo.getAuthor().getNickname());
        assertEquals(1, vo.getViewCount()); // 查看后计数+1
    }

    @Test
    @DisplayName("获取草稿文章 - 抛出异常")
    void testGetArticleById_Draft_ThrowsException() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("草稿");
        request.setContentMd("内容");
        request.setStatus(0);
        Long articleId = articleService.createArticle(request, testUserId);

        // When & Then
        assertThrows(BizException.class, () -> {
            articleService.getArticleById(articleId);
        });
    }

    @Test
    @DisplayName("点赞文章 - 成功")
    void testLikeArticle_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When
        articleService.likeArticle(articleId, testUserId);

        // Then
        ArticleVO vo = articleService.getArticleById(articleId, testUserId);
        assertEquals(1, vo.getLikeCount());
        assertTrue(vo.getLiked());
    }

    @Test
    @DisplayName("取消点赞文章 - 成功")
    void testUnlikeArticle_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);
        articleService.likeArticle(articleId, testUserId);

        // When
        articleService.unlikeArticle(articleId, testUserId);

        // Then
        ArticleVO vo = articleService.getArticleById(articleId, testUserId);
        assertEquals(0, vo.getLikeCount());
        assertFalse(vo.getLiked());
    }

    @Test
    @DisplayName("列出已发布文章 - 成功")
    void testListArticles_Success() {
        // Given
        for (int i = 1; i <= 3; i++) {
            ArticleRequest request = new ArticleRequest();
            request.setTitle("文章" + i);
            request.setContentMd("内容" + i);
            request.setCategoryId(testCategoryId);
            request.setStatus(1);
            articleService.createArticle(request, testUserId);
        }

        // When
        PageResult<ArticleVO> result = articleService.listArticles(1, 10, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotal());
        assertEquals(3, result.getRecords().size());
    }

    @Test
    @DisplayName("按分类筛选文章 - 成功")
    void testListArticles_ByCategory() {
        // Given
        Category category2 = new Category();
        category2.setName("生活");
        category2.setSlug("life");
        categoryMapper.insert(category2);

        ArticleRequest request1 = new ArticleRequest();
        request1.setTitle("技术文章");
        request1.setContentMd("内容");
        request1.setCategoryId(testCategoryId);
        request1.setStatus(1);
        articleService.createArticle(request1, testUserId);

        ArticleRequest request2 = new ArticleRequest();
        request2.setTitle("生活文章");
        request2.setContentMd("内容");
        request2.setCategoryId(category2.getId());
        request2.setStatus(1);
        articleService.createArticle(request2, testUserId);

        // When
        PageResult<ArticleVO> result = articleService.listArticles(1, 10, testCategoryId, null, null, null);

        // Then
        assertEquals(1, result.getTotal());
        assertEquals("技术文章", result.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("按关键词搜索文章 - 成功")
    void testListArticles_ByKeyword() {
        // Given
        ArticleRequest request1 = new ArticleRequest();
        request1.setTitle("Java编程指南");
        request1.setContentMd("内容");
        request1.setStatus(1);
        articleService.createArticle(request1, testUserId);

        ArticleRequest request2 = new ArticleRequest();
        request2.setTitle("Python入门");
        request2.setContentMd("内容");
        request2.setStatus(1);
        articleService.createArticle(request2, testUserId);

        // When
        PageResult<ArticleVO> result = articleService.listArticles(1, 10, null, null, null, "Java");

        // Then
        assertEquals(1, result.getTotal());
        assertEquals("Java编程指南", result.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("列出我的草稿 - 成功")
    void testListMyDrafts_Success() {
        // Given
        ArticleRequest draft = new ArticleRequest();
        draft.setTitle("草稿1");
        draft.setContentMd("内容");
        draft.setStatus(0);
        articleService.createArticle(draft, testUserId);

        ArticleRequest published = new ArticleRequest();
        published.setTitle("已发布");
        published.setContentMd("内容");
        published.setStatus(1);
        articleService.createArticle(published, testUserId);

        // When
        PageResult<ArticleVO> result = articleService.listMyDrafts(testUserId, 1, 10);

        // Then
        assertEquals(1, result.getTotal());
        assertEquals("草稿1", result.getRecords().get(0).getTitle());
    }

    @Test
    @DisplayName("获取文章作者ID - 成功")
    void testGetArticleAuthorId_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When
        Long authorId = articleService.getArticleAuthorId(articleId);

        // Then
        assertEquals(testUserId, authorId);
    }

    @Test
    @DisplayName("记录浏览历史 - 成功")
    void testRecordBrowse_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);

        // When
        articleService.recordBrowse(articleId, testUserId);

        // Then
        PageResult<ArticleVO> footprints = articleService.listFootprints(testUserId, 1, 10);
        assertEquals(1, footprints.getTotal());
    }

    @Test
    @DisplayName("列出点赞的文章 - 成功")
    void testListLikedArticles_Success() {
        // Given
        ArticleRequest request = new ArticleRequest();
        request.setTitle("测试文章");
        request.setContentMd("内容");
        request.setStatus(1);
        Long articleId = articleService.createArticle(request, testUserId);
        articleService.likeArticle(articleId, testUserId);

        // When
        PageResult<ArticleVO> result = articleService.listLikedArticles(testUserId, 1, 10);

        // Then
        assertEquals(1, result.getTotal());
        assertEquals("测试文章", result.getRecords().get(0).getTitle());
    }
}

