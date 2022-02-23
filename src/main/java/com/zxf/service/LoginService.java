package com.zxf.service;

import com.zxf.vo.Result;
import com.zxf.vo.params.LoginParams;

public interface LoginService {
    Result login(LoginParams loginParams);
}
