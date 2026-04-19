package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Apply;
import com.example.agricommunity.entity.AuditLog;
import com.example.agricommunity.mapper.ApplyMapper;
import com.example.agricommunity.mapper.AuditLogMapper;
import com.example.agricommunity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/apply")
public class ApplyController {

    @Autowired
    private ApplyMapper applyMapper;

    @Autowired
    private UserMapper userMapper;

    // 🌟 注入审计日志 Mapper，用来实现论文要求的回溯功能
    @Autowired
    private AuditLogMapper auditLogMapper;

    // 用户提交申请
    @PostMapping("/submit")
    public Result<String> submitApply(@RequestBody Apply apply) {
        applyMapper.insertApply(apply);
        return Result.success("申请已提交，请等待管理员审核");
    }

    // 管理员查看申请列表
    @GetMapping("/list")
    public Result<List<Apply>> getApplyList() {
        return Result.success(applyMapper.selectAllApplies());
    }

    // 管理员审批
    @PostMapping("/audit")
    @Transactional // 开启事务，保证更新状态、修改角色、记录日志同时成功或同时失败
    public Result<String> auditApply(@RequestParam Long id, @RequestParam Integer status, @RequestParam Long userId, @RequestParam Integer role) {
        // 1. 更新资质申请表的状态
        applyMapper.updateStatus(id, status, "");

        if (status == 1) {
            // 2. 审批通过，升级用户的角色权限
            userMapper.updateRole(userId, role);

            // 🌟 3. 记录审计日志 (满足论文 5.3 章节要求)
            AuditLog log = new AuditLog();
            log.setAdminId(1L); // 假设当前执行操作的超管ID为1
            log.setTargetId(userId);
            log.setActionType(role == 1 ? "通过农户入驻申请" : "通过团长入驻申请");
            log.setCreateTime(new Date());
            auditLogMapper.insert(log);

            return Result.success("已通过申请，用户权限已升级");
        } else {
            // 🌟 记录驳回日志
            AuditLog log = new AuditLog();
            log.setAdminId(1L);
            log.setTargetId(userId);
            log.setActionType("驳回入驻申请");
            log.setCreateTime(new Date());
            auditLogMapper.insert(log);

            return Result.success("已驳回申请");
        }
    }
}