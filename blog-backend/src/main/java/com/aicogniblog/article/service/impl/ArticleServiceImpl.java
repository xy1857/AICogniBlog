package com.aicogniblog.article.service.impl;

import com.aicogniblog.article.dto.ArticleEditVO;
import com.aicogniblog.article.dto.ArticleRequest;
import com.aicogniblog.article.dto.ArticleVO;
import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.entity.ArticleLike;
import com.aicogniblog.article.entity.ArticleTag;
import com.aicogniblog.article.entity.BrowseHistory;
import com.aicogniblog.article.entity.Category;
import com.aicogniblog.article.entity.Tag;
import com.aicogniblog.article.mapper.ArticleLikeMapper;
import com.aicogniblog.article.mapper.ArticleMapper;
import com.aicogniblog.article.mapper.BrowseHistoryMapper;
import com.aicogniblog.article.mapper.ArticleTagMapper;
import com.aicogniblog.article.mapper.CategoryMapper;
import com.aicogniblog.article.mapper.TagMapper;
import com.aicogniblog.article.service.ArticleService;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.comment.mapper.CommentMapper;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    private static final Parser MD_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    @Override
    public PageResult<ArticleVO> listArticles(int page, int size, Integer categoryId, String categorySlug, Integer tagId, String keyword) {
        Integer resolvedCategoryId = categoryId;
        if (resolvedCategoryId == null && StringUtils.hasText(categorySlug)) {
            Category bySlug = categoryMapper.selectOne(
                    new LambdaQueryWrapper<Category>().eq(Category::getSlug, categorySlug).last("LIMIT 1"));
            if (bySlug != null) resolvedCategoryId = bySlug.getId();
        }

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getPublishedAt);

        if (resolvedCategoryId != null) queryWrapper.eq(Article::getCategoryId, resolvedCategoryId);
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword);
        }

        if (tagId != null) {
            // 通过标签过滤文章ID
            List<ArticleTag> articleTags = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId));
            if (articleTags.isEmpty()) return PageResult.of(new Page<>());
            List<Long> articleIds = articleTags.stream().map(ArticleTag::getArticleId).toList();
            queryWrapper.in(Article::getId, articleIds);
        }

        Page<Article> articlePage = articleMapper.selectPage(new Page<>(page, size), queryWrapper);

        IPage<ArticleVO> voPage = articlePage.convert(this::toVOWithoutContent);
        return PageResult.of(voPage);
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        return getArticleById(id, null);
    }

    @Override
    public ArticleVO getArticleById(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() == 0) {
            throw new BizException(404, "文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        if (userId != null) {
            recordBrowse(id, userId);
        }
        ArticleVO vo = toVO(article, true);
        long likeCount = articleLikeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>().eq(ArticleLike::getArticleId, id));
        vo.setLikeCount(likeCount);
        if (userId != null) {
            long liked = articleLikeMapper.selectCount(
                    new LambdaQueryWrapper<ArticleLike>()
                            .eq(ArticleLike::getArticleId, id)
                            .eq(ArticleLike::getUserId, userId));
            vo.setLiked(liked > 0);
        }
        return vo;
    }

    @Override
    public void likeArticle(Long articleId, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == 0) {
            throw new BizException(404, "文章不存在");
        }
        long exists = articleLikeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, articleId)
                        .eq(ArticleLike::getUserId, userId));
        if (exists > 0) return;
        ArticleLike like = new ArticleLike();
        like.setUserId(userId);
        like.setArticleId(articleId);
        like.setCreatedAt(LocalDateTime.now());
        articleLikeMapper.insert(like);
    }

    @Override
    public void unlikeArticle(Long articleId, Long userId) {
        articleLikeMapper.delete(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, articleId)
                        .eq(ArticleLike::getUserId, userId));
    }

    @Override
    public PageResult<ArticleVO> listLikedArticles(Long userId, int page, int size) {
        Page<ArticleLike> likePage = new Page<>(page, size);
        articleLikeMapper.selectPage(likePage,
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getUserId, userId)
                        .orderByDesc(ArticleLike::getCreatedAt));
        List<ArticleLike> likes = likePage.getRecords();
        List<Long> articleIds = likes.stream().map(ArticleLike::getArticleId).toList();
        if (articleIds.isEmpty()) return PageResult.of(new Page<>());
        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        Map<Long, Article> articleMap = articles.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        List<ArticleVO> records = articleIds.stream()
                .map(articleMap::get)
                .filter(a -> a != null && a.getStatus() == 1)
                .map(this::toVOWithoutContent)
                .toList();
        PageResult<ArticleVO> result = new PageResult<>();
        result.setTotal(likePage.getTotal());
        result.setCurrent((long) page);
        result.setPages(likePage.getPages());
        result.setRecords(records);
        return result;
    }

    @Override
    public PageResult<ArticleVO> listArticlesBySubscription(List<Integer> categoryIds, List<Integer> tagIds, int page, int size) {
        List<Long> articleIds = new ArrayList<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Article> byCategory = articleMapper.selectList(
                    new LambdaQueryWrapper<Article>()
                            .eq(Article::getStatus, 1)
                            .in(Article::getCategoryId, categoryIds)
                            .select(Article::getId, Article::getPublishedAt));
            articleIds.addAll(byCategory.stream().map(Article::getId).toList());
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> byTag = articleTagMapper.selectList(
                    new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, tagIds));
            articleIds.addAll(byTag.stream().map(ArticleTag::getArticleId).distinct().toList());
        }
        articleIds = articleIds.stream().distinct().toList();
        if (articleIds.isEmpty()) return PageResult.of(new Page<>());
        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        articles = articles.stream().filter(a -> a.getStatus() == 1).sorted(Comparator.comparing(Article::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
        int total = articles.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<Article> pageList = from < total ? articles.subList(from, to) : List.of();
        List<ArticleVO> records = pageList.stream().map(this::toVOWithoutContent).toList();
        PageResult<ArticleVO> result = new PageResult<>();
        result.setTotal(total);
        result.setCurrent((long) page);
        result.setPages((total + size - 1) / size);
        result.setRecords(records);
        return result;
    }

    @Override
    public void recordBrowse(Long articleId, Long userId) {
        BrowseHistory existing = browseHistoryMapper.selectOne(
                new LambdaQueryWrapper<BrowseHistory>()
                        .eq(BrowseHistory::getUserId, userId)
                        .eq(BrowseHistory::getArticleId, articleId));
        if (existing != null) {
            browseHistoryMapper.delete(
                    new LambdaQueryWrapper<BrowseHistory>()
                            .eq(BrowseHistory::getUserId, userId)
                            .eq(BrowseHistory::getArticleId, articleId));
        }
        BrowseHistory bh = new BrowseHistory();
        bh.setUserId(userId);
        bh.setArticleId(articleId);
        bh.setViewedAt(LocalDateTime.now());
        browseHistoryMapper.insert(bh);
    }

    @Override
    public PageResult<ArticleVO> listFootprints(Long userId, int page, int size) {
        Page<BrowseHistory> historyPage = new Page<>(page, size);
        browseHistoryMapper.selectPage(historyPage,
                new LambdaQueryWrapper<BrowseHistory>()
                        .eq(BrowseHistory::getUserId, userId)
                        .orderByDesc(BrowseHistory::getViewedAt));
        List<BrowseHistory> list = historyPage.getRecords();
        List<Long> articleIds = list.stream().map(BrowseHistory::getArticleId).toList();
        if (articleIds.isEmpty()) return PageResult.of(new Page<>());
        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        Map<Long, Article> articleMap = articles.stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        List<ArticleVO> records = articleIds.stream()
                .map(articleMap::get)
                .filter(a -> a != null && a.getStatus() == 1)
                .map(this::toVOWithoutContent)
                .toList();
        PageResult<ArticleVO> result = new PageResult<>();
        result.setTotal(historyPage.getTotal());
        result.setCurrent((long) page);
        result.setPages(historyPage.getPages());
        result.setRecords(records);
        return result;
    }

    @Override
    @Transactional
    public Long createArticle(ArticleRequest request, Long authorId) {
        Article article = buildArticle(request, authorId);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);
        saveArticleTags(article.getId(), request.getTagIds());
        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleRequest request) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BizException(404, "文章不存在");

        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContentMd(request.getContentMd());
        article.setContentHtml(renderMarkdown(request.getContentMd()));
        article.setCoverUrl(request.getCoverUrl());
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus());
        if (request.getStatus() == 1 && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);

        articleTagMapper.deleteByArticleId(id);
        saveArticleTags(id, request.getTagIds());
    }

    @Override
    public void deleteArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BizException(404, "文章不存在");
        articleMapper.deleteById(id);
    }

    @Override
    public PageResult<ArticleVO> listMyDrafts(Long userId, int page, int size) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getAuthorId, userId)
                .eq(Article::getStatus, 0)
                .orderByDesc(Article::getUpdatedAt);
        Page<Article> articlePage = articleMapper.selectPage(new Page<>(page, size), queryWrapper);
        IPage<ArticleVO> voPage = articlePage.convert(this::toVOWithoutContent);
        return PageResult.of(voPage);
    }

    @Override
    public Long getArticleAuthorId(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BizException(404, "文章不存在");
        return article.getAuthorId();
    }

    @Override
    public ArticleEditVO getArticleForEdit(Long id, Long userId, boolean isAdmin) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BizException(404, "文章不存在");
        if (!isAdmin && !article.getAuthorId().equals(userId)) {
            throw new BizException(403, "无权限编辑该文章");
        }
        ArticleEditVO vo = new ArticleEditVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContentMd(article.getContentMd());
        vo.setCoverUrl(article.getCoverUrl());
        vo.setCategoryId(article.getCategoryId());
        vo.setStatus(article.getStatus());
        List<ArticleTag> ats = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        vo.setTagIds(ats.stream().map(ArticleTag::getTagId).toList());
        return vo;
    }

    private Article buildArticle(ArticleRequest request, Long authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContentMd(request.getContentMd());
        article.setContentHtml(renderMarkdown(request.getContentMd()));
        article.setCoverUrl(request.getCoverUrl());
        article.setCategoryId(request.getCategoryId());
        article.setStatus(request.getStatus());
        article.setViewCount(0);
        if (request.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        return article;
    }

    private String renderMarkdown(String md) {
        Node document = MD_PARSER.parse(md);
        return HTML_RENDERER.render(document);
    }

    private void saveArticleTags(Long articleId, List<Integer> tagIds) {
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagIds.forEach(tagId -> articleTagMapper.insert(new ArticleTag(articleId, tagId)));
        }
    }

    // 新增方法：不包含内容的转换
    private ArticleVO toVOWithoutContent(Article article) {
        return toVO(article, false);
    }

    private ArticleVO toVO(Article article, boolean includeContent) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setCoverUrl(article.getCoverUrl());
        vo.setStatus(article.getStatus());
        vo.setViewCount(article.getViewCount());
        vo.setPublishedAt(article.getPublishedAt());
        vo.setCreatedAt(article.getCreatedAt());

        if (includeContent) {
            vo.setContentHtml(article.getContentHtml());
        }

        // 作者信息
        User author = userMapper.selectById(article.getAuthorId());
        if (author != null) {
            ArticleVO.AuthorInfo authorInfo = new ArticleVO.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatarBase64(author.getAvatarBase64());
            vo.setAuthor(authorInfo);
        }

        // 分类信息
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                ArticleVO.CategoryInfo categoryInfo = new ArticleVO.CategoryInfo();
                categoryInfo.setId(category.getId());
                categoryInfo.setName(category.getName());
                categoryInfo.setSlug(category.getSlug());
                vo.setCategory(categoryInfo);
            }
        }

        // 标签信息
        List<Tag> tags = tagMapper.selectByArticleId(article.getId());
        vo.setTags(tags.stream().map(ArticleVO.TagInfo::from).toList());

        // 评论数
        long commentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<com.aicogniblog.comment.entity.Comment>()
                        .eq(com.aicogniblog.comment.entity.Comment::getArticleId, article.getId())
                        .eq(com.aicogniblog.comment.entity.Comment::getStatus, 1));
        vo.setCommentCount(commentCount);

        // 点赞数
        long likeCount = articleLikeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>().eq(ArticleLike::getArticleId, article.getId()));
        vo.setLikeCount(likeCount);

        return vo;
    }
}
