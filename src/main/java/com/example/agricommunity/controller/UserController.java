package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.User;
import com.example.agricommunity.service.UserService;
import com.example.agricommunity.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 处理登录请求，并在成功后颁发 JWT Token
     * 测试路径: POST http://localhost:8080/user/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        try {
// 1. 调用 Service 层执行具体的登录校验逻辑
            User user = userService.login(loginUser.getUsername(), loginUser.getPassword());

            // 👇 新增判空拦截：如果账号密码错误，返回错误提示
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 2. 登录成功，生成 Token
            String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole()); // 注意：User实体里叫role，不是roleType

            // 3. 将用户信息和 Token 一并打包放入 Map 中返回给前端
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);

            // 4. 返回成功结果
            return Result.success("登录成功", data);

        } catch (RuntimeException e) {
            // 捕获 Service 层抛出的异常（如账号不存在、密码错误）
            return Result.error(e.getMessage());
        }
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        String msg = userService.register(user);
        if ("注册成功".equals(msg)) {
            return Result.success(msg);
        }
        return Result.error(msg);
    }
}