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
    private String buyerName;
    private List<OrderItem> items;

    // 🌟 补全这个关键的 ID 字段，让 OrderService 里的 getLeaderId() 不再报错
    private Long leaderId;

    private String leaderName;
    private String leaderPhone;
    private String pickupAddress;
    private Date receiveTime;
    private String refundReason;
}