package com.zxf.service;

import com.zxf.pojo.Comment;
import com.zxf.vo.Result;
import com.zxf.vo.params.CommentParams;

import java.util.List;

public interface CommentsService {

    Result comments(Long id);

    /**
     * 根据文章id查找comments列表
     */
    List<Comment> findCommentsByArticleId(Long id);

    /**
     * 添加评论
     */
    Result createComment(CommentParams commentParams);
}
