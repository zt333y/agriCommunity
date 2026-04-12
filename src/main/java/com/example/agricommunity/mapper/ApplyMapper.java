package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Apply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ApplyMapper {
    @Insert("INSERT INTO t_apply(user_id, apply_role, real_name, id_card, address) " +
            "VALUES(#{userId}, #{applyRole}, #{realName}, #{idCard}, #{address})")
    int insertApply(Apply apply);

    @Select("SELECT a.*, u.username FROM t_apply a LEFT JOIN sys_user u ON a.user_id = u.id ORDER BY a.create_time DESC")
    List<Apply> selectAllApplies();

    @Update("UPDATE t_apply SET status = #{status}, reason = #{reason} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("reason") String reason);
}