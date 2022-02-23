package com.zxf.utils;


import org.springframework.beans.BeanUtils;

public class Pojo2VoUtils {

    public static <F,T> T pojo2PojoVo(F pojo,Class<T> aclass) throws InstantiationException, IllegalAccessException {

        T destination = aclass.newInstance();

        BeanUtils.copyProperties(pojo,destination);

        return destination;
    }
}
