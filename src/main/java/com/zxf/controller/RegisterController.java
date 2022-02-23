package com.zxf.controller;

import com.zxf.service.RegisterService;
import com.zxf.vo.Result;
import com.zxf.vo.params.RegisterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public Result register(@RequestBody RegisterParams registerParams){
        Result result = registerService.register(registerParams);
        return result;
    }
}
