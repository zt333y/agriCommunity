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

        // 🌟 1. 核心修改：获取“今日”的销售额和订单量
        Double todaySales = orderMapper.sumTodaySales();
        stats.put("totalSales", todaySales != null ? todaySales : 0.0); // 防止今日0单返回 null
        stats.put("totalOrders", orderMapper.countTodayOrders());

        // （在售商品数和总用户数依然保留大盘数据）
        stats.put("totalProducts", productMapper.countTotalProducts());
        stats.put("totalUsers", userMapper.countTotalUsers());

        // 🌟 2. 核心修改：改为统计“今日订单”中的农产品分类销量占比
        stats.put("categoryData", orderMapper.selectTodayCategoryStats());

        // 近七天趋势保持不变
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
        log.setCreateTime(new Date());

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