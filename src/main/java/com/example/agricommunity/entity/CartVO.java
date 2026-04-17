package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    private Long cartId;      // 购物车记录的ID
    private Long productId;   // 商品ID
    private String productName; // 商品名称（从t_product表连表查出来）
    private BigDecimal price;   // 商品单价
    private Integer quantity; // 购买数量
    private String imageUrl;
}