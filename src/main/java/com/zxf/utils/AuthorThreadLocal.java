package com.zxf.utils;

import com.zxf.pojo.Author;

public class AuthorThreadLocal {

    private AuthorThreadLocal(){}
    //线程变量隔离
    private static final ThreadLocal<Author> LOCAL = new ThreadLocal<>();

    public static void put(Author author){
        LOCAL.set(author);
    }
    public static Author get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}
