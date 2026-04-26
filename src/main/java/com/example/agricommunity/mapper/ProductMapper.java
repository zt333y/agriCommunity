package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    // 查询所有待审核的商品 (status = 0)
    @Select("SELECT * FROM t_product WHERE status = 0")
    List<Product> selectPendingProducts();

    // 更新商品的状态 (如：1 为上架，3 为下架)
    @Update("UPDATE t_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 农户发布新商品（注意 status 默认为 0 待审核）
    @Insert("INSERT INTO t_product(farmer_id, name, category, price, stock, unit, image_url, description, status) " +
            "VALUES(#{farmerId}, #{name}, #{category}, #{price}, #{stock}, #{unit}, #{imageUrl}, #{description}, 0)")
    int insertProduct(Product product);

    @Select("SELECT COUNT(*) FROM t_product WHERE status = 1")
    int countTotalProducts();

    @Select("SELECT category as name, COUNT(*) as value FROM t_product GROUP BY category")
    List<Map<String, Object>> selectCategoryStats();

    // 查询属于某个农户的所有商品（无论审核状态）
    @Select("SELECT * FROM t_product WHERE farmer_id = #{farmerId} ORDER BY create_time DESC")
    List<Product> selectProductsByFarmerId(Long farmerId);

    @Delete("DELETE FROM t_product WHERE id = #{id}")
    int deleteById(Long id);

    // 修改商品信息的 SQL
    @Update("UPDATE t_product SET name=#{name}, category=#{category}, price=#{price}, stock=#{stock}, unit=#{unit}, description=#{description}, image_url=#{imageUrl} WHERE id=#{id} AND farmer_id=#{farmerId}")
    int updateProduct(Product product);

    // 核心修复：把具体的 SQL 逻辑全部交给 XML 文件处理
    List<Product> selectProductList(@Param("keyword") String keyword);

}