package com.zxf.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class Article {

    private Long id;
    private Integer commentCounts;
    private Long createDate;
    private String summary;
    private String content;
    private String contentHtml;
    private String title;
    private Integer viewCounts;
    private Integer weight;

    @TableField(select = false)
    private Author author;//应该要使用Vo对象，这里稍微简化一点了
    @TableField(select = false)
    private Category category;
    @TableField(select = false)
    private List<Tag> tags;
    @TableField(select = false)
    private List<Comment> comments;
}
