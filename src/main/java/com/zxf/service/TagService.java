package com.zxf.service;

import com.zxf.pojo.Tag;
import com.zxf.vo.Result;

import java.util.List;

public interface TagService {

    /**
     *根据文章id查询Tag的具体对象
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 最热标签
     */
    Result hotTags(Integer limit);
}
