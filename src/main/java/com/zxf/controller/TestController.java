package com.zxf.controller;

import com.zxf.pojo.Author;
import com.zxf.utils.AuthorThreadLocal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public String test(@RequestHeader String Authorization){
        Author author = AuthorThreadLocal.get();
        System.out.println(author);
        return "成功！";
    }
}
