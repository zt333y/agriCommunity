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
}