package com.example.agricommunity.entity;
import lombok.Data;
import java.util.Date;

@Data
public class Apply {
    private Long id;
    private Long userId;
    private Integer applyRole; // 1:农户, 2:团长
    private String realName;
    private String idCard;
    private String address;
    private Integer status;    // 0:待审, 1:通过, 2:驳回
    private String reason;
    private Date createTime;
    private String username;   // 辅助字段：展示用户名
}