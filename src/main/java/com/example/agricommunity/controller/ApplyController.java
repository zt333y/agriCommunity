package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Apply;
import com.example.agricommunity.mapper.ApplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apply")
public class ApplyController {
    @Autowired private ApplyMapper applyMapper;
    @Autowired
    private com.example.agricommunity.mapper.UserMapper userMapper;

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
    @Transactional
    public Result<String> auditApply(@RequestParam Long id, @RequestParam Integer status, @RequestParam Long userId, @RequestParam Integer role) {
        applyMapper.updateStatus(id, status, "");
        if (status == 1) {
            // 🌟 核心：审批通过，更新 sys_user 表中的角色
            userMapper.updateRole(userId, role);
        }
        return Result.success("审批操作成功");
    }
}