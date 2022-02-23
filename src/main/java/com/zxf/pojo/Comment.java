package com.zxf.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private Long createDate;

    //注：对于评论来说，只可能通过文章去展示评论，所以这个对象实际上基本上就是null
    //这里为了表结构和对象一一对应还是先写上，便于理解
    @TableField(select = false)
    private Article article;
    @TableField(select = false)
    private Author author;
    @TableField(select = false)
    private List<Comment> comments;//子评论

    private Integer level;

}
