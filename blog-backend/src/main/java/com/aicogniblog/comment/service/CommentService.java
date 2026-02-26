package com.aicogniblog.comment.service;

import com.aicogniblog.comment.dto.AdminCommentVO;
import com.aicogniblog.comment.dto.CommentRequest;
import com.aicogniblog.comment.dto.CommentVO;
import com.aicogniblog.comment.dto.LatestCommentVO;
import com.aicogniblog.comment.dto.MyCommentVO;
import com.aicogniblog.common.result.PageResult;

import java.util.List;

public interface CommentService {
    List<CommentVO> listComments(Long articleId);
    List<LatestCommentVO> listLatest(int limit);
    PageResult<MyCommentVO> listMyComments(Long userId, int page, int size);
    void submitComment(Long articleId, CommentRequest request, Long userId);
    PageResult<AdminCommentVO> adminListComments(int page, int size, Integer status);
    void approve(Long id);
    void reject(Long id);
    void deleteComment(Long id);
}
