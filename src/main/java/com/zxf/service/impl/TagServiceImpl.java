package com.zxf.service.impl;

import com.zxf.mapper.TagMapper;
import com.zxf.pojo.Tag;
import com.zxf.service.TagService;
import com.zxf.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> findTagsByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return tags;
    }

    @Override
    public Result hotTags(Integer limit) {
        List<Tag> tags = tagMapper.findHotTags(limit);
        return Result.success(tags);
    }
}
