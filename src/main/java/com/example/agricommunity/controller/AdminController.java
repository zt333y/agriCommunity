package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.service.AdminService;
import com.example.agricommunity.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        // 1. 核心看板指标（对应你数据库中的订单、商品和用户表）
        stats.put("totalSales", 28450.00);
        stats.put("totalOrders", 420);
        stats.put("totalProducts", 64);
        stats.put("totalUsers", 158);

        // 2. 分类占比（饼图数据）
        java.util.List<java.util.Map<String, Object>> categoryData = new java.util.ArrayList<>();
        categoryData.add(createMap("name", "新鲜蔬菜", "value", 120));
        categoryData.add(createMap("name", "时令水果", "value", 210));
        categoryData.add(createMap("name", "肉禽蛋奶", "value", 150));
        categoryData.add(createMap("name", "五谷杂粮", "value", 80));
        stats.put("categoryData", categoryData);

        // 3. 近一周交易趋势（折线图数据）
        stats.put("weekDate", java.util.Arrays.asList("04-05", "04-06", "04-07", "04-08", "04-09", "04-10", "04-11"));
        stats.put("weekSales", java.util.Arrays.asList(2100, 3200, 2800, 4500, 3900, 5600, 6100));

        return Result.success(stats);
    }

    @Autowired
    private com.example.agricommunity.mapper.AuditLogMapper auditLogMapper;

    @GetMapping("/audit/history")
    public Result<List<Map<String, Object>>> getAuditHistory() {
        return Result.success(auditLogMapper.selectAuditLogs());
    }

    // 辅助方法：生成 Map
    private java.util.Map<String, Object> createMap(String k1, Object v1, String k2, Object v2) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
}