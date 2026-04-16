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
    public String checkout(Long userId, String address) { // 🌟 接收 address
        // 1. 查询购物车商品 (代码不变...)
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) return "购物车是空的！";

        // 2. 计算金额 (代码不变...)
        BigDecimal total = BigDecimal.ZERO;
        for (CartVO item : cartList) {
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        // 3. 生成主订单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId);
        order.setLeaderId(2L);
        order.setCommunityId(1L);
        order.setTotalAmount(total);
        order.setStatus(0);

        order.setAddress(address); // 🌟 核心：将地址保存到订单对象中！

        orderMapper.insertOrder(order);

        // 4. 生成订单明细并清空购物车 (代码不变...)
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

    // 🌟 新增：【确认收货逻辑】
    public String receiveOrder(Long orderId) {
        // 调用 Mapper 将状态更新为 2（已完成）
        int rows = orderMapper.updateOrderStatus(orderId, 2);
        if (rows > 0) {
            return "收货成功";
        }
        return "收货失败，订单可能不存在";
    }
}