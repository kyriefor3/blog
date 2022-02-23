package com.zxf.service;

import com.zxf.vo.Result;
import com.zxf.vo.params.ArticleParams;
import com.zxf.vo.params.PageParams;

public interface ArticleService {

    Result listArticle(PageParams params);

    Result findArticleById(Long id);

    Result publishArticle(ArticleParams articleParams);
}
