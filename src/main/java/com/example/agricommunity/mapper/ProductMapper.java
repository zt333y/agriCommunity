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
    @Select("SELECT * FROM tb_product WHERE status = 0")
    List<Product> selectPendingProducts();

    // 更新商品的状态 (比如把 0 改成 1 也就是上架)
    @Update("UPDATE tb_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}