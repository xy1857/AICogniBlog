package com.aicogniblog.article.mapper;

import com.aicogniblog.article.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT t.id, t.name, COUNT(at.article_id) AS article_count " +
            "FROM tag t LEFT JOIN article_tag at ON t.id = at.tag_id " +
            "LEFT JOIN article a ON at.article_id = a.id AND a.deleted = 0 AND a.status = 1 " +
            "WHERE t.deleted = 0 GROUP BY t.id ORDER BY t.id")
    List<java.util.Map<String, Object>> selectWithArticleCount();

    @Select("SELECT t.id, t.name FROM tag t " +
            "JOIN article_tag at ON t.id = at.tag_id " +
            "WHERE at.article_id = #{articleId} AND t.deleted = 0")
    List<Tag> selectByArticleId(Long articleId);
}
