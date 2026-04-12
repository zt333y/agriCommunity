package com.example.agricommunity.service;

import com.example.agricommunity.entity.AuditLog;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.mapper.AuditLogMapper;
import com.example.agricommunity.mapper.OrderMapper;
import com.example.agricommunity.mapper.ProductMapper;
import com.example.agricommunity.mapper.UserMapper; // 🌟 引入 UserMapper
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
    private UserMapper userMapper; // 🌟 注入用于统计用户数量

    /**
     * 🌟 核心功能：获取大屏实时统计报表
     * 整合了来自订单、商品、用户多个维度的数据库真实数据
     */
    public Map<String, Object> getRealStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 顶部四个核心 KPI 指标
        // 销售额、订单总数、在售商品数、注册用户数
        stats.put("totalSales", orderMapper.sumTotalSales());
        stats.put("totalOrders", orderMapper.countTotalOrders());
        stats.put("totalProducts", productMapper.countTotalProducts());
        stats.put("totalUsers", userMapper.countTotalUsers());

        // 2. 农产品分类占比数据（饼图专用）
        // 期望格式: [{name: "蔬菜", value: 10}, ...]
        stats.put("categoryData", productMapper.selectCategoryStats());

        // 3. 近七天交易金额趋势（折线图专用）
        List<Map<String, Object>> trendData = orderMapper.selectLastSevenDaysSales();
        List<String> dates = new ArrayList<>();
        List<Object> sales = new ArrayList<>();

        // 将数据库查询的 List 拆解为 ECharts 需要的 X轴(日期) 和 Y轴(数值) 数组
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
        auditLogMapper.insertLog(log);

        return status == 1 ? "审核通过，商品已成功上架" : "已驳回该商品";
    }

    /**
     * 获取全平台宏观订单列表
     */
    public List<OrderVO> getAllPlatformOrders() {
        return orderMapper.selectAllOrders();
    }
}