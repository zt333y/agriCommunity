package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.service.AdminService;
import com.example.agricommunity.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 🌟 新增注入 OrderService，用来处理发货
    @Autowired
    private OrderService orderService;

    // --- 1. 获取待审核商品列表 ---
    @GetMapping("/product/pending")
    public Result<List<Product>> getPendingProducts() {
        return Result.success(adminService.getPendingProducts());
    }

    // --- 2. 提交审核结果 ---
    @PostMapping("/product/audit")
    public Result<String> auditProduct(Long productId, Integer status, Long adminId) {
        if (adminId == null) adminId = 99L;
        try {
            String msg = adminService.auditProduct(productId, status, adminId);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // --- 3. 宏观订单监控 (🌟 修改路径为 /order/list 对齐 Vue) ---
    @GetMapping("/order/list")
    public Result<List<OrderVO>> getAllOrders() {
        return Result.success(adminService.getAllPlatformOrders());
    }

    // --- 4. 🌟 新增：一键发货接口 ---
    @PostMapping("/order/ship")
    public Result<String> shipOrder(Long orderId) {
        try {
            String msg = orderService.shipOrder(orderId);
            if ("发货成功".equals(msg)) {
                return Result.success(msg);
            }
            return Result.error(msg);
        } catch (Exception e) {
            return Result.error("发货异常：" + e.getMessage());
        }
    }
}