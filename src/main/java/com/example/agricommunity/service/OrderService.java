package com.example.agricommunity.service;

import com.example.agricommunity.entity.CartVO;
import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.mapper.CartMapper;
import com.example.agricommunity.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.agricommunity.entity.OrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderMapper orderMapper;

    // @Transactional 表示开启事务，一旦报错，所有数据库操作都会回滚（撤销），防止钱扣了订单没生成！
    @Transactional
    public String checkout(Long userId) {
        // 1. 查询该用户购物车里的所有商品
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) {
            return "购物车是空的！";
        }

        // 2. 计算总金额
        BigDecimal total = BigDecimal.ZERO;
        for (CartVO item : cartList) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }

        // 3. 生成主订单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "")); // 生成唯一流水号
        order.setUserId(userId);
        order.setLeaderId(2L); // 假定送到ID为2的团长那里
        order.setCommunityId(1L); // 假定送到ID为1的阳光小区
        order.setTotalAmount(total);
        order.setStatus(1); // 假设直接付款成功，状态变为待发货
        orderMapper.insertOrder(order);
        // 此时 order.getId() 已经有值了！

        // 4. 生成订单明细
        for (CartVO cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId()); // 绑定刚才生成的主订单ID
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setFarmerId(3L); // 假定商品都是ID为3的农户的
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            orderMapper.insertOrderItem(orderItem);
        }

        // 5. 清空该用户的购物车
        cartMapper.deleteCartByUserId(userId);

        return "下单成功";
    }

    public List<OrderVO> getOrderList(Long userId) {
        return orderMapper.selectOrderList(userId);
    }
}