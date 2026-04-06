package com.example.agricommunity.service;

import com.example.agricommunity.entity.AuditLog;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.Product;
import com.example.agricommunity.mapper.AuditLogMapper;
import com.example.agricommunity.mapper.OrderMapper;
import com.example.agricommunity.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 获取待审核商品列表
     */
    public List<Product> getPendingProducts() {
        return productMapper.selectPendingProducts();
    }

    /**
     * 审核商品（核心逻辑）
     * @param productId 商品ID
     * @param status 1:审核通过(上架)  2:审核驳回
     * @param adminId 当前操作的管理员ID
     */
    @Transactional
    public String auditProduct(Long productId, Integer status, Long adminId) {
        // 1. 更新商品状态
        int rows = productMapper.updateStatus(productId, status);
        if (rows == 0) {
            throw new RuntimeException("商品不存在或已被删除");
        }

        // 2. 强一致性记录操作日志 (亮点设计！)
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