package com.aicogniblog.article.controller;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.common.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ArticleController 单元测试
 * 使用 Mock 对象，不依赖 Spring 容器和数据库
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("文章控制器单元测试")
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ArticleController articleController;

    private ArticleVO testArticleVO;
    private ArticleEditVO testArticleEditVO;
    private PageResult<ArticleVO> testPageResult;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testArticleVO = new ArticleVO();
        testArticleVO.setId(1L);
        testArticleVO.setTitle("测试文章");
        testArticleVO.setSummary("测试摘要");
        testArticleVO.setContentHtml("<h1>测试内容</h1>");
        testArticleVO.setViewCount(100);
        testArticleVO.setLikeCount(10);
        testArticleVO.setCreatedAt(LocalDateTime.now());
        
        ArticleVO.AuthorInfo author = new ArticleVO.AuthorInfo();
        author.setId(1L);
        author.setNickname("测试作者");
        testArticleVO.setAuthor(author);

        ArticleVO.CategoryInfo category = new ArticleVO.CategoryInfo();
        category.setId(1);
        category.setName("技术");
        testArticleVO.setCategory(category);

        testArticleEditVO = new ArticleEditVO();
        testArticleEditVO.setId(1L);
        testArticleEditVO.setTitle("测试文章");
        testArticleEditVO.setContentMd("# 测试内容");
        testArticleEditVO.setCategoryId(1);
        testArticleEditVO.setTagIds(List.of(1));

        testPageResult = new PageResult<>();
        testPageResult.setRecords(List.of(testArticleVO));
        testPageResult.setTotal(1L);
        testPageResult.setCurrent(1);
        testPageResult.setPages(1);
    }

    @Test
    @DisplayName("列出文章 - 成功")
    void testListArticles_Success() {
        // Given
        when(articleService.listArticles(1, 10, null, null, null, null))
                .thenReturn(testPageResult);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listArticles(
                1, 10, null, null, null, null);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getTotal()).isEqualTo(1L);
        assertThat(result.getData().getRecords()).hasSize(1);
        assertThat(result.getData().getRecords().get(0).getTitle()).isEqualTo("测试文章");
        
        verify(articleService).listArticles(1, 10, null, null, null, null);
    }

    @Test
    @DisplayName("列出文章 - 按分类筛选")
    void testListArticles_ByCategory() {
        // Given
        when(articleService.listArticles(1, 10, 1, null, null, null))
                .thenReturn(testPageResult);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listArticles(
                1, 10, 1, null, null, null);

        // Then
        assertThat(result.getData().getRecords().get(0).getCategory().getId()).isEqualTo(1);
        verify(articleService).listArticles(1, 10, 1, null, null, null);
    }

    @Test
    @DisplayName("列出文章 - 按关键词搜索")
    void testListArticles_ByKeyword() {
        // Given
        when(articleService.listArticles(1, 10, null, null, null, "Java"))
                .thenReturn(testPageResult);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listArticles(
                1, 10, null, null, null, "Java");

        // Then
        assertThat(result.getData().getTotal()).isEqualTo(1L);
        verify(articleService).listArticles(1, 10, null, null, null, "Java");
    }

    @Test
    @DisplayName("获取文章详情 - 已登录用户")
    void testGetArticle_WithAuth() {
        // Given
        Long userId = 1L;
        when(authentication.getPrincipal()).thenReturn(userId);
        when(articleService.getArticleById(1L, userId)).thenReturn(testArticleVO);

        // When
        Result<ArticleVO> result = articleController.getArticle(1L, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getId()).isEqualTo(1L);
        assertThat(result.getData().getTitle()).isEqualTo("测试文章");
        
        verify(articleService).getArticleById(1L, userId);
    }

    @Test
    @DisplayName("获取文章详情 - 未登录用户")
    void testGetArticle_WithoutAuth() {
        // Given
        when(articleService.getArticleById(1L, null)).thenReturn(testArticleVO);

        // When
        Result<ArticleVO> result = articleController.getArticle(1L, null);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getId()).isEqualTo(1L);
        
        verify(articleService).getArticleById(1L, null);
    }

    @Test
    @DisplayName("创建文章 - 成功")
    void testCreateArticle_Success() {
        // Given
        Long userId = 1L;
        ArticleRequest request = new ArticleRequest();
        request.setTitle("新文章");
        request.setContentMd("# 内容");
        request.setStatus(1);

        when(authentication.getPrincipal()).thenReturn(userId);
        when(articleService.createArticle(request, userId)).thenReturn(1L);

        // When
        Result<Map<String, Long>> result = articleController.createArticle(request, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("创建成功");
        assertThat(result.getData().get("id")).isEqualTo(1L);
        
        verify(articleService).createArticle(request, userId);
    }

    @Test
    @DisplayName("更新文章 - 作者本人")
    void testUpdateArticle_ByAuthor() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;
        ArticleRequest request = new ArticleRequest();
        request.setTitle("更新标题");
        request.setContentMd("# 更新内容");
        request.setStatus(1);

        when(authentication.getPrincipal()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(articleService.getArticleAuthorId(articleId)).thenReturn(userId);
        doNothing().when(articleService).updateArticle(articleId, request);

        // When
        Result<Void> result = articleController.updateArticle(articleId, request, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("更新成功");
        
        verify(articleService).updateArticle(articleId, request);
    }

    @Test
    @DisplayName("更新文章 - 无权限")
    void testUpdateArticle_Forbidden() {
        // Given
        Long userId = 2L;
        Long authorId = 1L;
        Long articleId = 1L;
        ArticleRequest request = new ArticleRequest();
        request.setTitle("尝试更新");
        request.setContentMd("# 内容");
        request.setStatus(1);

        when(authentication.getPrincipal()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(articleService.getArticleAuthorId(articleId)).thenReturn(authorId);

        // When & Then
        assertThatThrownBy(() -> articleController.updateArticle(articleId, request, authentication))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("无权限编辑该文章");
        
        verify(articleService, never()).updateArticle(any(), any());
    }

    @Test
    @DisplayName("删除文章 - 作者本人")
    void testDeleteArticle_ByAuthor() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;

        when(authentication.getPrincipal()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(articleService.getArticleAuthorId(articleId)).thenReturn(userId);
        doNothing().when(articleService).deleteArticle(articleId);

        // When
        Result<Void> result = articleController.deleteArticle(articleId, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("删除成功");
        
        verify(articleService).deleteArticle(articleId);
    }

    @Test
    @DisplayName("删除文章 - 无权限")
    void testDeleteArticle_Forbidden() {
        // Given
        Long userId = 2L;
        Long authorId = 1L;
        Long articleId = 1L;

        when(authentication.getPrincipal()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(articleService.getArticleAuthorId(articleId)).thenReturn(authorId);

        // When & Then
        assertThatThrownBy(() -> articleController.deleteArticle(articleId, authentication))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("无权限删除该文章");
        
        verify(articleService, never()).deleteArticle(any());
    }

    @Test
    @DisplayName("点赞文章 - 成功")
    void testLikeArticle_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;

        when(authentication.getPrincipal()).thenReturn(userId);
        doNothing().when(articleService).likeArticle(articleId, userId);

        // When
        Result<Void> result = articleController.likeArticle(articleId, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("已点赞");
        
        verify(articleService).likeArticle(articleId, userId);
    }

    @Test
    @DisplayName("取消点赞 - 成功")
    void testUnlikeArticle_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;

        when(authentication.getPrincipal()).thenReturn(userId);
        doNothing().when(articleService).unlikeArticle(articleId, userId);

        // When
        Result<Void> result = articleController.unlikeArticle(articleId, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("已取消");
        
        verify(articleService).unlikeArticle(articleId, userId);
    }

    @Test
    @DisplayName("获取编辑数据 - 普通用户")
    void testGetArticleForEdit_ByUser() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;

        when(authentication.getPrincipal()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(articleService.getArticleForEdit(articleId, userId, false))
                .thenReturn(testArticleEditVO);

        // When
        Result<ArticleEditVO> result = articleController.getArticleForEdit(articleId, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getTitle()).isEqualTo("测试文章");
        assertThat(result.getData().getContentMd()).isEqualTo("# 测试内容");
        
        verify(articleService).getArticleForEdit(articleId, userId, false);
    }

    @Test
    @DisplayName("列出草稿 - 成功")
    void testListMyDrafts_Success() {
        // Given
        Long userId = 1L;
        PageResult<ArticleVO> draftPage = new PageResult<>();
        draftPage.setRecords(List.of(testArticleVO));
        draftPage.setTotal(1L);

        when(authentication.getPrincipal()).thenReturn(userId);
        when(articleService.listMyDrafts(userId, 1, 10)).thenReturn(draftPage);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listMyDrafts(1, 10, authentication);

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getTotal()).isEqualTo(1L);
        
        verify(articleService).listMyDrafts(userId, 1, 10);
    }

    @Test
    @DisplayName("列出分类 - 成功")
    void testListCategories_Success() {
        // Given
        List<Map<String, Object>> categories = List.of(
                Map.of("id", 1, "name", "技术", "articleCount", 10),
                Map.of("id", 2, "name", "生活", "articleCount", 5)
        );
        when(categoryMapper.selectWithArticleCount()).thenReturn(categories);

        // When
        Result<List<Map<String, Object>>> result = articleController.listCategories();

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).get("name")).isEqualTo("技术");
        
        verify(categoryMapper).selectWithArticleCount();
    }

    @Test
    @DisplayName("列出标签 - 成功")
    void testListTags_Success() {
        // Given
        List<Map<String, Object>> tags = List.of(
                Map.of("id", 1, "name", "Java", "articleCount", 15),
                Map.of("id", 2, "name", "Python", "articleCount", 8)
        );
        when(tagMapper.selectWithArticleCount()).thenReturn(tags);

        // When
        Result<List<Map<String, Object>>> result = articleController.listTags();

        // Then
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).get("name")).isEqualTo("Java");
        
        verify(tagMapper).selectWithArticleCount();
    }

    @Test
    @DisplayName("列出文章 - 使用默认分页参数")
    void testListArticles_DefaultPagination() {
        // Given
        when(articleService.listArticles(1, 10, null, null, null, null))
                .thenReturn(testPageResult);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listArticles(
                1, 10, null, null, null, null);

        // Then
        assertThat(result.getData().getCurrent()).isEqualTo(1);
        assertThat(result.getData().getPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("列出文章 - 空结果")
    void testListArticles_EmptyResult() {
        // Given
        PageResult<ArticleVO> emptyPage = new PageResult<>();
        emptyPage.setRecords(Collections.emptyList());
        emptyPage.setTotal(0L);
        
        when(articleService.listArticles(1, 10, null, null, null, null))
                .thenReturn(emptyPage);

        // When
        Result<PageResult<ArticleVO>> result = articleController.listArticles(
                1, 10, null, null, null, null);

        // Then
        assertThat(result.getData().getTotal()).isEqualTo(0L);
        assertThat(result.getData().getRecords()).isEmpty();
    }
}

