package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Cart;
import com.example.agricommunity.entity.CartVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CartMapper {
    // 插入购物车数据
    int insertCart(Cart cart);
    List<CartVO> selectCartList(Long userId);

    void deleteCartByUserId(Long userId);
}