package com.zxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxf.pojo.Category;
import com.zxf.pojo.Tag;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据文章id查询category的具体信息
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    Category findCategoryByArticleId(Long articleId);

}
