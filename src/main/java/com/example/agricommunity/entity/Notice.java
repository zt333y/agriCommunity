package com.example.agricommunity.entity;
import lombok.Data;
import java.util.Date;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Long adminId;
    private Date createTime;
}