package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // --- 1. 获取待审核商品列表 ---
    @GetMapping("/product/pending")
    public Result<List<Product>> getPendingProducts() {
        return Result.success(adminService.getPendingProducts());
    }

    // --- 2. 提交审核结果 ---
    // 测试路径: POST http://localhost:8080/api/admin/product/audit?productId=10&status=1&adminId=99
    @PostMapping("/product/audit")
    public Result<String> auditProduct(Long productId, Integer status, Long adminId) {
        // 开发测试阶段，防呆设计，默认给个超级管理员ID为99
        if (adminId == null) adminId = 99L;

        try {
            String msg = adminService.auditProduct(productId, status, adminId);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // --- 3. 宏观订单监控 ---
    @GetMapping("/order/all")
    public Result<List<OrderVO>> getAllOrders() {
        return Result.success(adminService.getAllPlatformOrders());
    }
}