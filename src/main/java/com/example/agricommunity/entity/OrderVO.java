package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status;
    private Date createTime;
    private String productNames;
    private String address;
    private Long productId;
    private String buyerName; // 🌟 接收查询出来的买家姓名

    // 🌟 核心新增：商品明细列表
    private List<OrderItem> items;
}