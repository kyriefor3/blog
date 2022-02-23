package com.zxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxf.pojo.Author;

public interface AuthorMapper extends BaseMapper<Author> {
    /**
     * 根据文章id查找author具体信息
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    Author findAuthorByArticleId(Long articleId);

    /**
     * 根据comment id 查找author具体信息
     */
    Author findAuthorByCommentId(Long commentId);
}
