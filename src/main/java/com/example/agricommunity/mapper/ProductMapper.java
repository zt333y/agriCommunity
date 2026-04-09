package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {
    // 获取所有上架的商品列表
    List<Product> selectProductList(@Param("keyword") String keyword);

    // 查询所有待审核的商品 (status = 0)
    @Select("SELECT * FROM t_product WHERE status = 0")
    List<Product> selectPendingProducts();

    // 更新商品的状态 (比如把 0 改成 1 也就是上架)
    @Update("UPDATE t_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 农户发布新商品（注意 status 默认为 0 待审核）
    @org.apache.ibatis.annotations.Insert("INSERT INTO t_product(farmer_id, name, category, price, stock, unit, image_url, description, status) " +
            "VALUES(#{farmerId}, #{name}, #{category}, #{price}, #{stock}, #{unit}, #{imageUrl}, #{description}, 0)")
    int insertProduct(com.example.agricommunity.entity.Product product);
}