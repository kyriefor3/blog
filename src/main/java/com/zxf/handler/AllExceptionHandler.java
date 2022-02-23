package com.zxf.handler;

import com.zxf.vo.Result;
import com.zxf.vo.enum4Error.ErrorCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//对加了@controller注解的方法进行拦截处理 Aop的实现
public class AllExceptionHandler {


    @ExceptionHandler(Exception.class)//所有异常
    @ResponseBody//返回前端JSON数据
    public Result doException(Exception exception){

        exception.printStackTrace();//打印异常信息
        // 其实当发生异常时，通常要处理异常，这是编程的好习惯，所以e.printStackTrace()可以方便你调试程序！

        return Result.fail(10000,"服务器正忙!");
    }
}
