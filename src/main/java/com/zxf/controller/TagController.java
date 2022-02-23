package com.zxf.controller;

import com.zxf.service.TagService;
import com.zxf.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("hot")
    public Result hotTags(){
        int limit = 2;
        Result res = tagService.hotTags(limit);
        return res;
    }

}
