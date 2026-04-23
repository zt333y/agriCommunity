package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.User;
import com.example.agricommunity.mapper.UserMapper;
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

    // 🌟 修复 1：这里必须加上 @Autowired！否则它是 null！
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        try {
            User user = userService.login(loginUser.getUsername(), loginUser.getPassword());

            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);

            return Result.success("登录成功", data);

        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            if (user.getRole() == null) {
                user.setRole(1);
            }
            String msg = userService.register(user);
            if ("注册成功".equals(msg)) {
                return Result.success(msg);
            } else {
                return Result.error(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("后端发生异常：" + e.getMessage());
        }
    }

    @PostMapping("/updateAddress")
    public Result<String> updateAddress(@RequestParam Long userId, @RequestParam String address) {
        boolean success = userService.updateAddress(userId, address);
        if (success) {
            return Result.success("收货地址更新成功");
        } else {
            return Result.error("更新失败，用户不存在");
        }
    }

    // 🌟 处理移动端发来的修改个人资料请求
    @PostMapping("/update")
    public Result<String> updateProfile(@RequestBody User user) {
        // 这里就不会再报空指针异常了
        userMapper.updateUser(user);
        return Result.success("资料修改成功");
    }
}