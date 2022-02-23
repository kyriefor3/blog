package com.zxf.controller;

import com.zxf.service.LogoutService;
import com.zxf.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logout")
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @GetMapping
    public Result logout(@RequestHeader String authorization){

        Result result = logoutService.logout(authorization);

        return result;
    }
}
