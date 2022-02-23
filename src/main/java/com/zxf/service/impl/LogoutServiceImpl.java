package com.zxf.service.impl;

import com.zxf.service.LogoutService;
import com.zxf.vo.Result;
import com.zxf.vo.enum4Error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Result logout(String authorization) {
        System.out.println(redisTemplate.delete(authorization));
        return (redisTemplate.delete("TOKEN_" + authorization))?
                Result.success(null):Result.fail(ErrorCode.LOGOUT_FAIL.getCode(),ErrorCode.LOGOUT_FAIL.getMsg());
    }
}
