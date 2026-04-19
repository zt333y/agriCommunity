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

    /**
     * 处理注册请求（修复 Integer 类型问题）
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            // 🌟 防御 1：因为 role 是 Integer 类型，只需判断是否为 null。
            // 默认给普通用户角色（假设 1 代表普通居民，如果是 0 请你自行改成 0）
            if (user.getRole() == null) {
                user.setRole(1);
            }

            // 执行注册业务逻辑
            String msg = userService.register(user);

            if ("注册成功".equals(msg)) {
                return Result.success(msg);
            } else {
                return Result.error(msg); // 可能是“用户名已存在”等业务提示
            }

        } catch (Exception e) {
            // 🌟 防御 2：哪怕数据库连不上、SQL 写错了，也绝对不能崩溃！
            e.printStackTrace();
            return Result.error("后端发生异常：" + e.getMessage());
        }
    }

    // 🌟 新增：收货地址更新接口
    @PostMapping("/updateAddress")
    public Result<String> updateAddress(@RequestParam Long userId, @RequestParam String address) {
        boolean success = userService.updateAddress(userId, address);
        if (success) {
            return Result.success("收货地址更新成功");
        } else {
            return Result.error("更新失败，用户不存在");
        }
    }

    // 🌟 新增：处理移动端发来的修改个人资料请求
    @PostMapping("/update")
    public Result<String> updateProfile(@RequestBody com.example.agricommunity.entity.User user) {
        userMapper.updateUser(user);
        return Result.success("资料修改成功");
    }
}