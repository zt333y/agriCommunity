package com.example.agricommunity.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ReviewVO {
    private Long id;
    private String username;     // 评价人姓名
    private String productName;  // 被评价的商品名
    private String content;      // 评价内容
    private Integer score;       // 评分 (1-5)
    private Date createTime;     // 评价时间
}