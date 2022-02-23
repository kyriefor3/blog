package com.zxf.controller;

import com.zxf.pojo.Author;
import com.zxf.utils.AuthorThreadLocal;
import com.zxf.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("authors")
public class AuthorController {

    @GetMapping("currentAuthor")
    public Result currentAuthor(@RequestHeader String authorization){

        //登录拦截器已经做了token解析的工作，并且把对象放在了ThreadLocal当中
        Author author = AuthorThreadLocal.get();

        //因为最后AuthorThreadLocal中的Author对象会被删除，所以这里进行复制
        Author author1 = new Author();
        BeanUtils.copyProperties(author,author1);
        return Result.success(author1);
    }
}
