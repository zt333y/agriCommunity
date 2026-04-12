package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交结算，生成订单
     */
    @PostMapping("/create")
    public Result<String> createOrder(HttpServletRequest request) {
        // 👉 彻底告别硬编码：通过拦截器提前解析并放好的属性，拿到真实的 userId！
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());

        // 执行具体的业务逻辑
        String msg = orderService.checkout(userId);
        if ("下单成功".equals(msg)) {
            return Result.success(msg);
        }
        return Result.error(msg);
    }

    /**
     * 查询当前登录用户的订单列表
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(HttpServletRequest request) {
        // 👉 同样的方式，拿到真实的 userId
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());

        return Result.success(orderService.getOrderList(userId));
    }

    /**
     * 确认收货接口
     */
    @PostMapping("/receive")
    public Result<String> receiveOrder(Long orderId) {
        try {
            String msg = orderService.receiveOrder(orderId);
            if ("收货成功".equals(msg)) {
                return Result.success(msg);
            }
            return Result.error(msg);
        } catch (Exception e) {
            return Result.error("收货异常：" + e.getMessage());
        }
    }

    // ... 保留原有的下单、查询接口 ...

    // 🌟 1. 团长获取社区订单列表
    @GetMapping("/leaderList")
    public Result<List<OrderVO>> getLeaderOrders() {
        // 简化处理：实际业务中应根据团长所在社区过滤，这里暂返回全部订单供团长操作
        return Result.success(orderMapper.selectAllOrders());
    }

    // 🌟 2. 团长确认到货 (入库)：农户发货(1) -> 团长签收(4:待提货)
    @PostMapping("/arrive")
    public Result<String> arriveOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 4);
        return Result.success("入库成功，已通知居民前来提货");
    }

    // 🌟 3. 团长核销提货 (出库)：待提货(4) -> 交易完成/待评价(2)
    @PostMapping("/verify")
    public Result<String> verifyOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 2);
        return Result.success("核销成功，订单已完成流转");
    }
}