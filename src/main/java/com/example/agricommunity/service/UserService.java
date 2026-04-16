package com.example.agricommunity.service;

import com.example.agricommunity.entity.User;
import com.example.agricommunity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 登录逻辑
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    // 注册逻辑
    public String register(User user) {
        // 1. 先查用户名是不是被占用了
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return "用户名已存在";
        }
        // 2. 没占用，就存进去
        userMapper.insertUser(user);
        return "注册成功";
    }

    // 🌟 新增：更新地址业务逻辑
    public boolean updateAddress(Long userId, String address) {
        return userMapper.updateAddress(userId, address) > 0;
    }
}