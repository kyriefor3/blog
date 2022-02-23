package com.zxf.service.impl;

import com.alibaba.fastjson.JSON;
import com.zxf.pojo.Author;
import com.zxf.service.AuthorService;
import com.zxf.service.RegisterService;
import com.zxf.utils.JWTUtils;
import com.zxf.vo.Result;
import com.zxf.vo.enum4Error.ErrorCode;
import com.zxf.vo.params.RegisterParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 注册功能
     * 根据account查询用户，是否存在
     *      存在，返回错误信息
     *      不存在，
     *          1.将用户信息保存在mysql
     *          2.用id生成token，返回客户端，并保存在redis中
     *                                              key:TOKEN_xxx
     *                                              value:JSON形式的author信息
     */
    @Override
    public Result register(RegisterParams registerParams) {
        String account = registerParams.getAccount();
        String password = registerParams.getPassword();
        String nickname = registerParams.getNickname();

        Author author = authorService.findAuthorByAccount(account);

        //用户存在，返回错误信息
        if(author!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        //用户不存在
        // 保存用户数据，其中密码要经过加密
        String salt = "zxf2kyrie!@#zzz";//加密盐
        Author authorNew = new Author();
        authorNew.setAccount(account);
        authorNew.setPassword(DigestUtils.md5Hex(password+salt));
        authorNew.setNickname(nickname);
        authorService.save(authorNew);//1.mp生成id策略：雪花算法 2.mp插入操作后会自动进行id回写

        // 生成token，并存在redis中
        String token = JWTUtils.createToken(authorNew.getId());
        redisTemplate.opsForValue()
                .set("TOKEN_"+token, JSON.toJSONString(authorNew),100, TimeUnit.DAYS);
        return Result.success(token);
    }
}
