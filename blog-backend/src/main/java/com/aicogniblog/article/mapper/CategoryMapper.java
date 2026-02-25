package com.aicogniblog.article.mapper;

import com.aicogniblog.article.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT c.id, c.name, c.slug, COUNT(a.id) AS article_count " +
            "FROM category c LEFT JOIN article a ON c.id = a.category_id AND a.deleted = 0 AND a.status = 1 " +
            "WHERE c.deleted = 0 GROUP BY c.id ORDER BY c.id")
    List<java.util.Map<String, Object>> selectWithArticleCount();
}
