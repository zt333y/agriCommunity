package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Product;
// 👇 补上 ProductMapper 的导包
import com.example.agricommunity.mapper.ProductMapper;
import com.example.agricommunity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
// 🌟 接收前端的 keyword 参数（非必填）
    @GetMapping("/list")
    public Result<List<Product>> getProductList(@RequestParam(required = false) String keyword) {
        List<Product> list = productMapper.selectProductList(keyword);
        return Result.success("获取成功", list);
    }

    // 🌟 新增：手机端调用发布商品的接口
    @PostMapping("/add")
    public Result<String> addProduct(@RequestBody Product product) {
        try {
            String msg = productService.addProduct(product);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error("后端发生异常：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录农户发布的商品列表
     */
    @GetMapping("/my")
    public Result<List<Product>> getMyProducts(jakarta.servlet.http.HttpServletRequest request) {
        // 从拦截器中获取真实的登录用户 ID (此时该用户角色应该是农户)
        Long farmerId = Long.valueOf(request.getAttribute("currentUserId").toString());
        return Result.success(productService.getMyProducts(farmerId));
    }

    @PostMapping("/delete")
    public Result<String> deleteProduct(Long id) {
        try {
            String msg = productService.deleteProduct(id);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error("删除异常：" + e.getMessage());
        }
    }

    // 🌟 新增：修改商品接口
    @PostMapping("/update")
    public Result<String> updateProduct(@RequestBody Product product) {
        int rows = productMapper.updateProduct(product);
        return rows > 0 ? Result.success("修改成功") : Result.error("修改失败，可能无权限");
    }
}