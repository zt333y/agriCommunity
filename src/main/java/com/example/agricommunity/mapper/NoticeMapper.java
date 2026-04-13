package com.example.agricommunity.mapper;
import com.example.agricommunity.entity.Notice;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NoticeMapper {
    @Insert("INSERT INTO t_notice(title, content, admin_id) VALUES(#{title}, #{content}, #{adminId})")
    int insert(Notice notice);

    @Select("SELECT * FROM t_notice ORDER BY create_time DESC")
    List<Notice> selectAll();

    @Delete("DELETE FROM t_notice WHERE id = #{id}")
    int deleteById(Long id);
}