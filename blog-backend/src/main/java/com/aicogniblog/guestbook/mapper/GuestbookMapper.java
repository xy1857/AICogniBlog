package com.aicogniblog.guestbook.mapper;

import com.aicogniblog.guestbook.entity.Guestbook;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 留言板 Mapper
 */
@Mapper
public interface GuestbookMapper {
    
    @Select("SELECT * FROM guestbook WHERE deleted = 0 AND status = 1 ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<Guestbook> selectPage(@Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT COUNT(*) FROM guestbook WHERE deleted = 0 AND status = 1")
    int count();
    
    @Insert("INSERT INTO guestbook (user_id, name, email, content, avatar_url, ip_address, status, deleted, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{email}, #{content}, #{avatarUrl}, #{ipAddress}, #{status}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Guestbook guestbook);
    
    @Update("UPDATE guestbook SET deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM guestbook WHERE id = #{id} AND deleted = 0")
    Guestbook selectById(Long id);
}

