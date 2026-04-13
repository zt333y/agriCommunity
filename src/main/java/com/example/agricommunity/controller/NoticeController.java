package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Autowired
    private com.example.agricommunity.mapper.NoticeMapper noticeMapper;

    @GetMapping("/list")
    public Result<List<Notice>> getList() {
        return Result.success(noticeMapper.selectAll());
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Notice notice) {
        noticeMapper.insert(notice);
        return Result.success("公告发布成功");
    }

    @PostMapping("/delete")
    public Result<String> delete(Long id) {
        noticeMapper.deleteById(id);
        return Result.success("公告已删除");
    }
}