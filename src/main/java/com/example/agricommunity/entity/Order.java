package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    private Long id;
    private String orderNo;      // 订单流水号
    private Long userId;         // 谁买的
    private Long leaderId;       // 哪个团长负责（测试固定写死）
    private Long communityId;    // 送到哪个社区（测试固定写死）
    private BigDecimal totalAmount; // 订单总金额
    private Integer status;      // 状态：0待付款，1待发货
    private Date createTime;
    private String address;
}