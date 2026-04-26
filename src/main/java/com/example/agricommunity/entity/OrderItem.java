package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;        // 归属哪个主订单
    private Long productId;      // 商品ID
    private Long farmerId;       // 哪个农户的（测试固定写死）
    private String productName;  // 购买时的名称
    private BigDecimal productPrice; // 购买时的单价
    private Integer quantity;    // 数量
    private BigDecimal totalPrice; // 此项总价
    private String imageUrl;
}