package com.zxf.vo.params;

import com.zxf.pojo.Category;
import com.zxf.pojo.Tag;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParams {


    private ArticleBody body;
    //TIP:因为前端写文章使用markdown编辑器，
    // 所以不仅有String content，还会有String bodyHtml


    private Category category;

    private String summary;

    private List<Tag> tags;

    private String title;
}
