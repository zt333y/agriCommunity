package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    // 获取所有上架的商品列表
    List<Product> selectProductList(@Param("keyword") String keyword);
}