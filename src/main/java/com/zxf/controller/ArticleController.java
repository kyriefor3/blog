package com.zxf.controller;

import com.zxf.common.aop.LogAnnotation;
import com.zxf.service.ArticleService;
import com.zxf.vo.Result;
import com.zxf.vo.params.ArticleParams;
import com.zxf.vo.params.PageParams;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    //@LogAnnotation
    public Result listArticle(@RequestBody PageParams params){
        return articleService.listArticle(params);
    }

    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable Long id){
        return articleService.findArticleById(id);
    }

    @PostMapping("publish")//发布文章，需要先登录，所以需要将路径加到登录拦截器中
    public Result publishArticle(@RequestBody ArticleParams articleParams){
        return articleService.publishArticle(articleParams);
    }
}
