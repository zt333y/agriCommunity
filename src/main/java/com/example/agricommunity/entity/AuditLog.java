package com.example.agricommunity.entity;

import java.util.Date;

public class AuditLog {
    private Long id;
    private Long adminId;
    private Long targetId; // 被操作的对象ID（比如商品ID）
    private String actionType; // 动作标识：如 "AUDIT_PASS" 或 "AUDIT_REJECT"
    private Date createTime;

    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}