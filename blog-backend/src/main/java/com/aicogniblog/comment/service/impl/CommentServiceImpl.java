package com.aicogniblog.comment.service.impl;

import com.aicogniblog.ai.service.AiService;
import com.aicogniblog.article.entity.Article;
import com.aicogniblog.article.mapper.ArticleMapper;
import com.aicogniblog.auth.entity.User;
import com.aicogniblog.auth.mapper.UserMapper;
import com.aicogniblog.comment.dto.AdminCommentVO;
import com.aicogniblog.comment.dto.CommentRequest;
import com.aicogniblog.comment.dto.CommentVO;
import com.aicogniblog.comment.dto.LatestCommentVO;
import com.aicogniblog.comment.dto.MyCommentVO;
import com.aicogniblog.comment.entity.Comment;
import com.aicogniblog.comment.mapper.CommentMapper;
import com.aicogniblog.comment.service.CommentService;
import com.aicogniblog.common.exception.BizException;
import com.aicogniblog.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final AiService aiService;

    @Override
    public List<CommentVO> listComments(Long articleId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreatedAt));

        // 组装用户信息
        List<Long> userIds = comments.stream().map(Comment::getUserId).distinct().toList();
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));

        // 构建树形结构
        Map<Long, CommentVO> voMap = comments.stream()
                .collect(Collectors.toMap(Comment::getId, c -> toCommentVO(c, userMap)));

        List<CommentVO> roots = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVO vo = voMap.get(comment.getId());
            if (comment.getParentId() == null) {
                roots.add(vo);
            } else {
                CommentVO parent = voMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(vo);
                }
            }
        }
        return roots;
    }

    @Override
    public void submitComment(Long articleId, CommentRequest request, Long userId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == 0) {
            throw new BizException(404, "文章不存在");
        }

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setStatus(0); // 待审核
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        // 异步触发 AI 审核
        aiService.auditCommentAsync(comment.getId(), comment.getContent(), article.getTitle());
    }

    @Override
    public PageResult<AdminCommentVO> adminListComments(int page, int size, Integer status) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreatedAt);
        if (status != null) queryWrapper.eq(Comment::getStatus, status);

        Page<Comment> commentPage = commentMapper.selectPage(new Page<>(page, size), queryWrapper);

        List<Long> userIds = commentPage.getRecords().stream().map(Comment::getUserId).distinct().toList();
        List<Long> articleIds = commentPage.getRecords().stream().map(Comment::getArticleId).distinct().toList();

        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Article> articleMap = articleIds.isEmpty() ? Map.of() :
                articleMapper.selectBatchIds(articleIds).stream().collect(Collectors.toMap(Article::getId, Function.identity()));

        // 修复类型转换问题：使用 IPage 接口类型
        IPage<AdminCommentVO> voPage = commentPage.convert(c -> toAdminVO(c, userMap, articleMap));
        return PageResult.of(voPage);
    }

    @Override
    public void approve(Long id) {
        Comment comment = getCommentOrThrow(id);
        comment.setStatus(1);
        commentMapper.updateById(comment);
    }

    @Override
    public void reject(Long id) {
        Comment comment = getCommentOrThrow(id);
        comment.setStatus(2);
        commentMapper.updateById(comment);
    }

    @Override
    public void deleteComment(Long id) {
        getCommentOrThrow(id);
        commentMapper.deleteById(id);
    }

    @Override
    public List<LatestCommentVO> listLatest(int limit) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getStatus, 1)
                        .orderByDesc(Comment::getCreatedAt)
                        .last("LIMIT " + Math.min(limit, 100)));
        if (comments.isEmpty()) return List.of();

        List<Long> userIds = comments.stream().map(Comment::getUserId).distinct().toList();
        List<Long> articleIds = comments.stream().map(Comment::getArticleId).distinct().toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Article> articleMap = articleMapper.selectBatchIds(articleIds).stream().collect(Collectors.toMap(Article::getId, Function.identity()));

        return comments.stream().map(c -> toLatestVO(c, userMap, articleMap)).toList();
    }

    @Override
    public PageResult<MyCommentVO> listMyComments(Long userId, int page, int size) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserId, userId)
                .orderByDesc(Comment::getCreatedAt);
        Page<Comment> commentPage = commentMapper.selectPage(new Page<>(page, size), queryWrapper);
        List<Long> articleIds = commentPage.getRecords().stream().map(Comment::getArticleId).distinct().toList();
        Map<Long, Article> articleMap = articleIds.isEmpty() ? Map.of() :
                articleMapper.selectBatchIds(articleIds).stream().collect(Collectors.toMap(Article::getId, Function.identity()));
        IPage<MyCommentVO> voPage = commentPage.convert(c -> toMyCommentVO(c, articleMap));
        return PageResult.of(voPage);
    }

    private MyCommentVO toMyCommentVO(Comment comment, Map<Long, Article> articleMap) {
        MyCommentVO vo = new MyCommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setArticleId(comment.getArticleId());
        Article article = articleMap.get(comment.getArticleId());
        if (article != null) vo.setArticleTitle(article.getTitle());
        return vo;
    }

    private LatestCommentVO toLatestVO(Comment comment, Map<Long, User> userMap, Map<Long, Article> articleMap) {
        LatestCommentVO vo = new LatestCommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setCreatedAt(comment.getCreatedAt());
        User user = userMap.get(comment.getUserId());
        if (user != null) {
            LatestCommentVO.UserBrief ub = new LatestCommentVO.UserBrief();
            ub.setId(user.getId());
            ub.setNickname(user.getNickname());
            vo.setUser(ub);
        }
        Article article = articleMap.get(comment.getArticleId());
        if (article != null) {
            LatestCommentVO.ArticleBrief ab = new LatestCommentVO.ArticleBrief();
            ab.setId(article.getId());
            ab.setTitle(article.getTitle());
            vo.setArticle(ab);
        }
        return vo;
    }

    private Comment getCommentOrThrow(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) throw new BizException(404, "评论不存在");
        return comment;
    }

    private CommentVO toCommentVO(Comment comment, Map<Long, User> userMap) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setReplies(new ArrayList<>());

        User user = userMap.get(comment.getUserId());
        if (user != null) {
            CommentVO.UserInfo userInfo = new CommentVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatarBase64(user.getAvatarBase64());
            vo.setUser(userInfo);
        }
        return vo;
    }

    private AdminCommentVO toAdminVO(Comment comment, Map<Long, User> userMap, Map<Long, Article> articleMap) {
        AdminCommentVO vo = new AdminCommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setAiAuditResult(comment.getAiAuditResult());
        vo.setAiReplySuggestion(comment.getAiReplySuggestion());
        vo.setCreatedAt(comment.getCreatedAt());

        User user = userMap.get(comment.getUserId());
        if (user != null) {
            AdminCommentVO.UserBrief userBrief = new AdminCommentVO.UserBrief();
            userBrief.setId(user.getId());
            userBrief.setNickname(user.getNickname());
            vo.setUser(userBrief);
        }

        Article article = articleMap.get(comment.getArticleId());
        if (article != null) {
            AdminCommentVO.ArticleBrief articleBrief = new AdminCommentVO.ArticleBrief();
            articleBrief.setId(article.getId());
            articleBrief.setTitle(article.getTitle());
            vo.setArticle(articleBrief);
        }
        return vo;
    }
}
