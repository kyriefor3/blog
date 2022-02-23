package com.zxf.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zxf.pojo.Author;
import com.zxf.pojo.Category;
import com.zxf.pojo.Comment;
import com.zxf.pojo.Tag;
import lombok.Data;

import java.util.List;

@Data
@TableName("ms_article")
public class ArticleVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer commentCounts;
    private Long createDate;
    private String summary;
    private String content;
    private String contentHtml;
    private String title;
    private Integer viewCounts;
    private Integer weight;


    private Long authorId;

    private Integer categoryId;


}
