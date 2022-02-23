package com.zxf.service.impl;

import com.alibaba.fastjson.JSON;
import com.zxf.pojo.Author;
import com.zxf.service.AuthorService;
import com.zxf.service.LoginService;
import com.zxf.utils.JWTUtils;
import com.zxf.vo.Result;
import com.zxf.vo.enum4Error.ErrorCode;
import com.zxf.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result login(LoginParams loginParams) {
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String salt = "zxf2kyrie!@#zzz";//加密盐
        String password_actual = DigestUtils.md5Hex(password + salt);

        Author author = authorService.findAuthor(account,password_actual);

        //用户不存在，返回错误信息
        if (author==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        //用户存在，根据id生成token，返回客户端，并保存在redis中
        String token = JWTUtils.createToken(author.getId());
        redisTemplate.opsForValue()
                .set("TOKEN_"+token, JSON.toJSONString(author),100, TimeUnit.DAYS);
        return Result.success(token);
    }
}
