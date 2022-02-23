package com.zxf.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zxf.pojo.Author;
import com.zxf.utils.AuthorThreadLocal;
import com.zxf.utils.JWTUtils;
import com.zxf.vo.Result;
import com.zxf.vo.enum4Error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 登录拦截器
     * 对需要登录才能访问的资源进行拦截，检查token的合法性
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        //（1）检查token是否为空
        if(StringUtils.isBlank(token)){
            returnError(response,ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            return false;
        }


        //（2）根据token合法性
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }

        String authorJson =  redisTemplate.opsForValue().get("TOKEN_"+token);
        if (StringUtils.isBlank(authorJson)) {
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }

        //解析出author对象
        Author author = JSON.parseObject(authorJson, Author.class);
        if (author == null){
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }


        //把author对象放进ThreadLocal
        AuthorThreadLocal.put(author);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //一定要在结束后删除，否则会有内存泄露风险
        AuthorThreadLocal.remove();
    }

    /**
     * 返回错误信息
     */
    public void returnError(HttpServletResponse response,int code,String msg) throws IOException {
        Result result = Result.fail(code,msg);
        //响应
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(result));
    }
}
