package com.zxf.service;

import com.zxf.pojo.Category;

public interface CategoryService {

    Category findCategoryByArticleId(Long articleId);
}
