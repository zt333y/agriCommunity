package com.example.agricommunity.service;

import com.example.agricommunity.entity.Product;
import com.example.agricommunity.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    // 👇 修改了这里：加上了 keyword 参数，并调用了全新的 Mapper 方法
    public List<Product> getProductList(String keyword) {
        return productMapper.selectProductList(keyword);
    }

    // 🌟 新增：处理发布商品逻辑
    public String addProduct(Product product) {
        // 开发测试阶段防呆设计：如果没有传农户ID，默认给它分配为数据库里叫“李大爷”的农户ID（3）
        if (product.getFarmerId() == null) {
            product.setFarmerId(3L);
        }
        int rows = productMapper.insertProduct(product);
        if (rows > 0) {
            return "发布成功，请等待平台审核";
        }
        return "发布失败，请稍后再试";
    }

    public List<Product> getMyProducts(Long farmerId) {
        return productMapper.selectProductsByFarmerId(farmerId);
    }

    public String deleteProduct(Long id) {
        int rows = productMapper.deleteById(id);
        return rows > 0 ? "删除成功" : "商品不存在";
    }
}