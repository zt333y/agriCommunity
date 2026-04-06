package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<String> createOrder(Long userId) {
        if (userId == null) userId = 1L; // 测试默认用用户1
        String msg = orderService.checkout(userId);
        if ("下单成功".equals(msg)) {
            return Result.success(msg);
        }
        return Result.error(msg);
    }

    @GetMapping("/list")
    public Result<java.util.List<com.example.agricommunity.entity.OrderVO>> getOrderList(Long userId) {
        if (userId == null) userId = 1L; // 测试默认用用户1
        return Result.success(orderService.getOrderList(userId));
    }
}