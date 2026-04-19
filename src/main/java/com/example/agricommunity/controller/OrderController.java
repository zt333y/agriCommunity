package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderItem;
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
    public Result<String> createOrder(HttpServletRequest request, @RequestParam("address") String address) {
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());
        // 🌟 把地址传给 Service
        String msg = orderService.checkout(userId, address);
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

    // 🌟 1. 农户专属功能：获取今日采摘/待发货汇总清单
    @GetMapping("/pickingList")
    public Result<List<com.example.agricommunity.entity.FarmerPickingVO>> getPickingList(HttpServletRequest request) {
        // 获取当前登录农户的真实 ID
        Long farmerId = Long.valueOf(request.getAttribute("currentUserId").toString());
        return Result.success(orderMapper.selectPickingList(farmerId));
    }

    // 🌟 2. 农户专属功能：标记订单为“已发货” (状态从 0 -> 1)
    // 实际业务中农户可以针对单个订单发货，或者通过扫码发货
    @PostMapping("/ship")
    public Result<String> shipOrder(Long orderId) {
        // 复用之前写好的更新状态 SQL
        orderMapper.updateStatus(orderId, 1);
        return Result.success("发货成功，已流转至社区团长端");
    }

    // 🌟 农户专属：按商品一键批量发货
    @PostMapping("/shipByProduct")
    public Result<String> shipByProduct(Long productId, HttpServletRequest request) {
        Long farmerId = Long.valueOf(request.getAttribute("currentUserId").toString());
        int rows = orderMapper.shipByProduct(farmerId, productId);
        if (rows > 0) {
            return Result.success("一键发货成功，商品已流转至社区团长端！");
        }
        return Result.error("暂无需要发货的订单");
    }

    // 🌟 新增：获取订单下的所有商品列表，用于多商品分别评价
    @GetMapping("/items")
    public Result<List<OrderItem>> getOrderItems(@RequestParam Long orderId) {
        return Result.success(orderMapper.selectItemsByOrderId(orderId));
    }
}