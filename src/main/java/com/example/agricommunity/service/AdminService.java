package com.example.agricommunity.service;

import com.example.agricommunity.entity.AuditLog;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.mapper.AuditLogMapper;
import com.example.agricommunity.mapper.OrderMapper;
import com.example.agricommunity.mapper.ProductMapper;
import com.example.agricommunity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取大屏实时统计报表
     */
    public Map<String, Object> getRealStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalSales", orderMapper.sumTotalSales());
        stats.put("totalOrders", orderMapper.countTotalOrders());
        stats.put("totalProducts", productMapper.countTotalProducts());
        stats.put("totalUsers", userMapper.countTotalUsers());

        stats.put("categoryData", productMapper.selectCategoryStats());

        List<Map<String, Object>> trendData = orderMapper.selectLastSevenDaysSales();
        List<String> dates = new ArrayList<>();
        List<Object> sales = new ArrayList<>();

        for (Map<String, Object> map : trendData) {
            dates.add(String.valueOf(map.get("date")));
            sales.add(map.get("sales"));
        }
        stats.put("weekDate", dates);
        stats.put("weekSales", sales);

        return stats;
    }

    /**
     * 获取待审核商品列表
     */
    public List<Product> getPendingProducts() {
        return productMapper.selectPendingProducts();
    }

    /**
     * 审核商品（核心逻辑）
     */
    @Transactional
    public String auditProduct(Long productId, Integer status, Long adminId) {
        // 1. 更新商品状态
        int rows = productMapper.updateStatus(productId, status);
        if (rows == 0) {
            throw new RuntimeException("商品不存在或已被删除");
        }

        // 2. 强一致性记录操作日志
        AuditLog log = new AuditLog();
        log.setAdminId(adminId);
        log.setTargetId(productId);
        log.setActionType(status == 1 ? "AUDIT_PASS" : "AUDIT_REJECT");
        log.setCreateTime(new Date()); // 🌟 补全时间字段

        // 🌟 核心修复：调用更新后的 insert 方法！
        auditLogMapper.insert(log);

        return status == 1 ? "审核通过，商品已成功上架" : "已驳回该商品";
    }

    /**
     * 获取全平台宏观订单列表
     */
    public List<OrderVO> getAllPlatformOrders() {
        return orderMapper.selectAllOrders();
    }
}