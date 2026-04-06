package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;      // 订单流水号
    private BigDecimal totalAmount; // 总金额
    private Integer status;      // 状态：0待付款，1待发货，2已发货等
    private Date createTime;     // 下单时间
    private String productNames; // 绝妙设计：把订单里的所有商品名拼成一个字符串返回给手机
}