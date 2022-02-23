package com.zxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxf.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    /**
     *根据文章id查询Tag的具体对象
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    List<Tag> findTagsByArticleId(Long id);

    /**
     * 最热标签
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    List<Tag> findHotTags(Integer limit);
}
