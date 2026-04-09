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
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId);
        order.setLeaderId(2L);
        order.setCommunityId(1L);
        order.setTotalAmount(total);
        // 🌟 修改点 1：将下单成功的初始状态改为 0（完美对应 Vue 里的"待发货"）
        order.setStatus(0);
        orderMapper.insertOrder(order);

        // 4. 生成订单明细
        for (CartVO cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setFarmerId(3L);
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

    // 🌟 修改点 2：新增【发货逻辑】
    public String shipOrder(Long orderId) {
        // 调用 Mapper 将状态更新为 1（已发货）
        int rows = orderMapper.updateOrderStatus(orderId, 1);
        if (rows > 0) {
            return "发货成功";
        }
        return "发货失败，订单可能不存在";
    }
}