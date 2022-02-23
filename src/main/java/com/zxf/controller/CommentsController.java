package com.zxf.controller;

import com.zxf.service.CommentsService;
import com.zxf.vo.Result;
import com.zxf.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping("article/{id}")
    public Result comments(@PathVariable Long id){
        Result result = commentsService.comments(id);
        return result;
    }

    @PostMapping("create/change")
    public Result createComment(@RequestBody CommentParams commentParams){
        return commentsService.createComment(commentParams);
    }
}
