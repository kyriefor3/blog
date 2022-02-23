package com.zxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxf.mapper.AuthorMapper;
import com.zxf.pojo.Author;
import com.zxf.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorMapper authorMapper;

    /**
     * 根据account查询用户表
     */
    @Override
    public Author findAuthorByAccount(String account) {
        QueryWrapper<Author> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        Author author = authorMapper.selectOne(wrapper);
        return author;

    }

    /**
     * 保存用户
     */
    @Override
    public void save(Author author) {
        authorMapper.insert(author);
    }

    @Override
    public Author findAuthor(String account, String password) {
        QueryWrapper<Author> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account).eq("password",password);
        Author author = authorMapper.selectOne(wrapper);
        return author;
    }

    @Override
    public Author findAuthorByArticleId(Long articleId) {
        Author author = authorMapper.findAuthorByArticleId(articleId);
        return author;
    }

    @Override
    public Author findAuthorByCommentId(Long commentId) {
        Author author = authorMapper.findAuthorByCommentId(commentId);
        return author;
    }
}
