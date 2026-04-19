package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;      // 订单流水号
    private BigDecimal totalAmount; // 总金额
    private Integer status;      // 状态：0待发货，1已发货，2已完成待评价等
    private Date createTime;     // 下单时间
    private String productNames; // 订单里的所有商品名
    private String address;      // 收货地址

    // 🌟 核心新增：商品ID，方便手机端评价
    private Long productId;
}