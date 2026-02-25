package com.aicogniblog.comment.service;

import com.aicogniblog.comment.dto.AdminCommentVO;
import com.aicogniblog.comment.dto.CommentRequest;
import com.aicogniblog.comment.dto.CommentVO;
import com.aicogniblog.common.result.PageResult;

import java.util.List;

public interface CommentService {
    List<CommentVO> listComments(Long articleId);
    void submitComment(Long articleId, CommentRequest request, Long userId);
    PageResult<AdminCommentVO> adminListComments(int page, int size, Integer status);
    void approve(Long id);
    void reject(Long id);
    void deleteComment(Long id);
}
