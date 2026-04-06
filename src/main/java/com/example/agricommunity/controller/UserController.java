package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.User;
import com.example.agricommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 登录接口
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        // 调用 Service 层的登录逻辑
        User loginUser = userService.login(user.getUsername(), user.getPassword());

        if (loginUser != null) {
            // 登录成功，返回 200 状态码，并把用户信息传给 APP
            return Result.success(loginUser);
        } else {
            // 登录失败，返回 500 状态码和错误提示
            return Result.error("账号或密码错误");
        }
    }

    // 2. 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 调用 Service 层的注册逻辑
        String msg = userService.register(user);

        if ("注册成功".equals(msg)) {
            return Result.success(msg);
        } else {
            return Result.error(msg);
        }
    }
}