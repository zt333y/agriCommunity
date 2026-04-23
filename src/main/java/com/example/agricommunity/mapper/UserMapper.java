package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    User login(@Param("username") String username, @Param("password") String password);

    User selectByUsername(@Param("username") String username);

    void insertUser(User user);

    @Select("SELECT COUNT(*) FROM sys_user")
    int countTotalUsers();

    @Update("UPDATE sys_user SET role = #{role} WHERE id = #{userId}")
    int updateRole(@Param("userId") Long userId, @Param("role") Integer role);

    @Update("UPDATE sys_user SET address = #{address} WHERE id = #{userId}")
    int updateAddress(@Param("userId") Long userId, @Param("address") String address);

    // 🌟 修复 2：彻底把 realName 改成了 username！和你的数据库字段、User类完美对应
    @Update("<script>" +
            "UPDATE sys_user " +
            "<set>" +
            "  <if test=\"username != null\">username = #{username},</if>" +
            "  <if test=\"password != null\">password = #{password},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateUser(User user);
}