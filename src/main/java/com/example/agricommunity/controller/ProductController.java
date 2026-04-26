package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Product;
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

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/list")
    public Result<List<Product>> getProductList(@RequestParam(required = false) String keyword) {
        List<Product> list = productMapper.selectProductList(keyword);
        return Result.success("获取成功", list);
    }

    @PostMapping("/add")
    public Result<String> addProduct(@RequestBody Product product) {
        try {
            String msg = productService.addProduct(product);
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error("后端发生异常：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<Product>> getMyProducts(jakarta.servlet.http.HttpServletRequest request) {
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

    @PostMapping("/update")
    public Result<String> updateProduct(@RequestBody Product product) {
        int rows = productMapper.updateProduct(product);
        return rows > 0 ? Result.success("修改成功") : Result.error("修改失败，可能无权限");
    }

    // 🌟 新增：商品上架/下架接口
    @PostMapping("/updateStatus")
    public Result<String> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        int rows = productMapper.updateStatus(id, status);
        if (rows > 0) {
            String msg = (status == 1) ? "商品已上架" : "商品已下架";
            return Result.success(msg);
        }
        return Result.error("操作失败");
    }
}