package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    @Insert("INSERT INTO t_audit_log(admin_id, target_id, action_type, create_time) " +
            "VALUES(#{adminId}, #{targetId}, #{actionType}, NOW())")
    int insertLog(AuditLog auditLog);
}