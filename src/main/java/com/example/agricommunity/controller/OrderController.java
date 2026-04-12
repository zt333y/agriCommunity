package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.service.OrderService;
import com.example.agricommunity.mapper.OrderMapper; // 🌟 必须手动导包
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper; // 🌟 之前你漏了这一句，导致下面所有方法都报红

    @PostMapping("/create")
    public Result<String> createOrder(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());
        String msg = orderService.checkout(userId);
        return "下单成功".equals(msg) ? Result.success(msg) : Result.error(msg);
    }

    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());
        return Result.success(orderService.getOrderList(userId));
    }

    @PostMapping("/receive")
    public Result<String> receiveOrder(Long orderId) {
        return Result.success(orderService.receiveOrder(orderId));
    }

    // 🌟 1. 团长获取社区订单列表
    @GetMapping("/leaderList")
    public Result<List<OrderVO>> getLeaderOrders() {
        return Result.success(orderMapper.selectAllOrders());
    }

    // 🌟 2. 团长确认到货 (入库)
    @PostMapping("/arrive")
    public Result<String> arriveOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 4);
        return Result.success("入库成功，已通知居民前来提货");
    }

    // 🌟 3. 团长核销提货 (出库)
    @PostMapping("/verify")
    public Result<String> verifyOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 2);
        return Result.success("核销成功，订单已完成流转");
    }
}