package com.example.agricommunity.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AuditLog {
    private Long id;
    private Long adminId;      // 执行操作的管理员ID
    private Long targetId;     // 被操作的对象ID (比如申请人的ID)
    private String actionType; // 动作标识 (比如 "通过农户入驻申请")
    private Date createTime;   // 发生时间
}