package com.example.agricommunity.entity;
import lombok.Data;
import java.util.Date;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long productId;
    private String content;
    private Integer score;
    private Date createTime;
}