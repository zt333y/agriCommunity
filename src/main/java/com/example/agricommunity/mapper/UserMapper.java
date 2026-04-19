package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 🌟 注意：这里不要写 @Select 注解，因为 SQL 已经在 UserMapper.xml 里写过了
    User login(@Param("username") String username, @Param("password") String password);

    // 🌟 这里也不要写 @Select，SQL 在 XML 里
    User selectByUsername(@Param("username") String username);

    // 🌟 这里也不要写 @Insert，SQL 在 XML 里
    void insertUser(User user);

    // 🌟 这个方法 XML 里没有，所以可以保留注解方式
    @Select("SELECT COUNT(*) FROM sys_user")
    int countTotalUsers();

    // 🌟 这个方法 XML 里没有，可以保留注解方式
    @Update("UPDATE sys_user SET role = #{role} WHERE id = #{userId}")
    int updateRole(@Param("userId") Long userId, @Param("role") Integer role);

    // 🌟 新增：更新用户收货地址
    @Update("UPDATE sys_user SET address = #{address} WHERE id = #{userId}")
    int updateAddress(@Param("userId") Long userId, @Param("address") String address);

    @Update("<script>" +
            "UPDATE sys_user " +
            "<set>" +
            "  <if test=\"username != null\">username = #{username},</if>" +
            "  <if test=\"password != null\">password = #{password},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateUser(com.example.agricommunity.entity.User user);
}