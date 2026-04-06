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
        // 存入数据库
        cartMapper.insertCart(cart);
        return Result.success("成功加入购物车！");
    }

    //获取购物车列表接口
    @GetMapping("/list")
    public Result<List<CartVO>> getList(Long userId) {
        // 如果前端没传 userId，为了测试方便，我们默认查询管理员(ID为1)的购物车
        if (userId == null) {
            userId = 1L;
        }
        List<CartVO> list = cartMapper.selectCartList(userId);
        return Result.success(list);
    }
}