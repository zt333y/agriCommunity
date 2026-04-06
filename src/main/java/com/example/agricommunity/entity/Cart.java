package com.example.agricommunity.entity;

import lombok.Data;

@Data
public class Cart {
    private Long id;
    private Long userId;     // 谁买的
    private Long productId;  // 买了啥
    private Integer quantity;// 买了几个
}