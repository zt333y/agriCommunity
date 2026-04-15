package com.example.agricommunity.entity;

import lombok.Data;

/**
 * 农户采摘/发货汇总清单视图对象
 */
@Data
public class FarmerPickingVO {
    private Long productId;
    private String productName;
    private Integer totalQuantity; // 需要采摘的总数量
    private String unit;           // 计量单位 (如: 斤, 箱)
}