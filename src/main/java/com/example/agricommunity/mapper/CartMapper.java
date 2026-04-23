package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Cart;
import com.example.agricommunity.entity.CartVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    // 原有的插入和查询列表方法
    int insertCart(Cart cart);
    List<CartVO> selectCartList(Long userId);
    void deleteCartByUserId(Long userId);

    // 🌟 新增：根据【用户ID】和【商品ID】查找购物车里是不是已经有这件商品了
    @Select("SELECT * FROM t_cart WHERE user_id = #{userId} AND product_id = #{productId}")
    Cart selectCartByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // 🌟 新增：如果有了，就直接在这个数据的原有数量上累加
    @Update("UPDATE t_cart SET quantity = quantity + #{quantity} WHERE id = #{id}")
    int updateCartQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    // 🌟 新增：直接把数量设置为传入的精确值（用于前端加减号）
    @Update("UPDATE t_cart SET quantity = #{quantity} WHERE id = #{cartId}")
    int setQuantityExact(@Param("cartId") Long cartId, @Param("quantity") Integer quantity);

    // 🌟 新增：根据购物车记录的 ID 删除商品
    @Delete("DELETE FROM t_cart WHERE id = #{cartId}")
    int deleteCartById(@Param("cartId") Long cartId);
}