package com.zxf.service;

import com.zxf.pojo.Author;
import com.zxf.vo.Result;

public interface AuthorService {
    /**
     * 根据account，查询author
     */
    Author findAuthorByAccount(String account);

    /**
     * 保存作者
     */
    void save(Author author);

    /**
     * 根据account，password查询作者
     */
    Author findAuthor(String account,String password);

    /**
     * 根据文章id查找author
     */
    Author findAuthorByArticleId(Long articleId);


    /**
     * 根据comment的id，查找作者信息
     */
    Author findAuthorByCommentId(Long commentId);
}
