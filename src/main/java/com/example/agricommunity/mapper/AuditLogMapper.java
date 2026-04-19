package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditLogMapper {

    // 🌟 修复：把表名彻底改回你数据库里真实的 t_audit_log
    @Insert("INSERT INTO t_audit_log(admin_id, target_id, action_type, create_time) " +
            "VALUES(#{adminId}, #{targetId}, #{actionType}, #{createTime})")
    int insert(AuditLog auditLog);

    // 🌟 修复：把表名改回 t_audit_log，把管理员名字关联改回 u.real_name
    @Select("SELECT l.*, u.real_name as admin_name FROM t_audit_log l " +
            "LEFT JOIN sys_user u ON l.admin_id = u.id " +
            "ORDER BY l.create_time DESC")
    List<Map<String, Object>> selectAuditLogs();
}