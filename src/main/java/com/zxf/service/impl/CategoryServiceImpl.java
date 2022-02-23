package com.zxf.service.impl;

import com.zxf.mapper.CategoryMapper;
import com.zxf.pojo.Category;
import com.zxf.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public Category findCategoryByArticleId(Long articleId) {
        Category category = categoryMapper.findCategoryByArticleId(articleId);
        return category;
    }
}
