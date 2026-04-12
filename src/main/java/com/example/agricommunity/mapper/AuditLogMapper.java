package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditLogMapper {
    @Insert("INSERT INTO t_audit_log(admin_id, target_id, action_type, create_time) " +
            "VALUES(#{adminId}, #{targetId}, #{actionType}, NOW())")
    int insertLog(AuditLog auditLog);

    @Select("SELECT l.*, u.real_name as admin_name FROM t_audit_log l " +
            "LEFT JOIN sys_user u ON l.admin_id = u.id " +
            "ORDER BY l.create_time DESC")
    List<Map<String, Object>> selectAuditLogs();
}