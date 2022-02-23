package com.zxf.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ms_article_tag")
public class ArticleTagVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Integer tagId;
}
