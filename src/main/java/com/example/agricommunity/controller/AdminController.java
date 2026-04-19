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
        // 🌟 彻底告别死数据，调用 Service 里的数据库查询逻辑
        try {
            return Result.success(adminService.getRealStats());
        } catch (Exception e) {
            return Result.error("统计数据抓取失败：" + e.getMessage());
        }
    }

    @Autowired
    private com.example.agricommunity.mapper.AuditLogMapper auditLogMapper;

    @GetMapping("/audit/history")
    public Result<List<Map<String, Object>>> getAuditHistory() {
        try {
            List<Map<String, Object>> logs = auditLogMapper.selectAuditLogs();
            return Result.success(logs);
        } catch (Exception e) {
            e.printStackTrace(); // 在控制台打印具体的错误信息，方便调试
            return Result.error("获取日志失败：" + e.getMessage());
        }
    }
    // 辅助方法：生成 Map
    private java.util.Map<String, Object> createMap(String k1, Object v1, String k2, Object v2) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
}