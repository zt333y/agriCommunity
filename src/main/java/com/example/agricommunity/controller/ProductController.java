package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Product;
// 👇 补上 ProductMapper 的导包
import com.example.agricommunity.mapper.ProductMapper;
import com.example.agricommunity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 👇 关键修复：把 productMapper 也注入进来！
    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取农产品列表接口
     * 访问地址：GET http://localhost:8080/api/product/list
     */
    @GetMapping("/list")
    public Result<List<Product>> getList(String keyword) {
        // 把前端传过来的搜索词交给数据库
        List<Product> list = productMapper.selectProductList(keyword);
        return Result.success(list);
    }
}