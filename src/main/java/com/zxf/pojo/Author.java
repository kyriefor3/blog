package com.zxf.pojo;

import lombok.Data;

@Data
public class Author {
    private Long id;
    private Long createDate;
    private String account;
    private String password;
    private String nickname;
    private String avatar;
    private Integer admin;
    private Integer deleted;
}
