package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Cart;
import com.example.agricommunity.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.agricommunity.entity.CartVO;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @PostMapping("/add")
    public Result<String> addCart(@RequestBody Cart cart) {
        // 如果前端没传数量，默认买 1 个
        if (cart.getQuantity() == null) {
            cart.setQuantity(1);
        }

        // 🌟 核心修复：加入之前，先去查一下这个用户是不是已经加过这个商品了？
        Cart existCart = cartMapper.selectCartByUserIdAndProductId(cart.getUserId(), cart.getProductId());

        if (existCart != null) {
            // 🌟 如果已经存在，直接更新数量 (例如原来的2个 + 这次加的1个 = 3个)
            cartMapper.updateCartQuantity(existCart.getId(), cart.getQuantity());
        } else {
            // 🌟 如果不存在，才作为新的一行数据插入数据库
            cartMapper.insertCart(cart);
        }

        return Result.success("成功加入购物车！");
    }

    //获取购物车列表接口
    @GetMapping("/list")
    public Result<List<CartVO>> getList(Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        List<CartVO> list = cartMapper.selectCartList(userId);
        return Result.success(list);
    }
}