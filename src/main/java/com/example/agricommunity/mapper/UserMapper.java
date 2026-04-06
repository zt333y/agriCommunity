package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 根据用户名和密码查用户（登录用）
    User login(@Param("username") String username, @Param("password") String password);

    // 检查用户名是否存在（注册用）
    User selectByUsername(@Param("username") String username);

    // 插入新用户（注册用）
    void insertUser(User user);
}