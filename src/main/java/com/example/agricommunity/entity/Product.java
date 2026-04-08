package com.example.agricommunity.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private Long farmerId;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String imageUrl;
    private String description;
    private Integer status;

}