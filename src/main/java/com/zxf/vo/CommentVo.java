package com.zxf.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zxf.pojo.*;
import lombok.Data;


@Data
@TableName("ms_comment")
public class CommentVo extends Comment{
    @TableId(type = IdType.AUTO)
    private Long id;
    /*
    private String content;

    private Long createDate;*/

    private Long articleId;

    private Long authorId;

    private Long parentId;
    /*
    //注：对于评论来说，只可能通过文章去展示评论，所以这个对象实际上基本上就是null
    //这里为了表结构和对象一一对应还是先写上，便于理解
    @TableField(select = false)
    private Article article;
    @TableField(select = false)
    private Author author;
    @TableField(select = false)
    private List<Comment> comments;//子评论
    */

    /*private Integer level;*/


}
