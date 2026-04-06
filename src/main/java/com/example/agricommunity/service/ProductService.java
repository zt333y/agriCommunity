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
}