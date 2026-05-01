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

@Service
public class OrderService {

    @Autowired private CartMapper cartMapper;
    @Autowired private OrderMapper orderMapper;

    @Transactional
    public String checkout(Long userId, String address) {
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) return "购物车是空的！";

        // 🌟 核心修复：购物车有几个商品，就拆分成几个独立的订单，完美解决各农户独立发货、状态互不干扰的问题！
        for (CartVO cartItem : cartList) {
            Order order = new Order();
            String timeStr = String.valueOf(System.currentTimeMillis());
            order.setOrderNo(timeStr.substring(timeStr.length() - 10) + (int)((Math.random() * 9 + 1) * 1000));
            order.setUserId(userId);
            order.setLeaderId(2L);
            order.setCommunityId(1L);

            BigDecimal itemTotalAmount = cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            order.setTotalAmount(itemTotalAmount);
            order.setStatus(0);
            order.setAddress(address);

            orderMapper.insertOrder(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            // 🌟 修复：不再写死 3L，而是动态获取该商品的真实农户 ID
            orderItem.setFarmerId(cartItem.getFarmerId() != null ? cartItem.getFarmerId() : 3L);
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(itemTotalAmount);
            orderMapper.insertOrderItem(orderItem);
        }

        cartMapper.deleteCartByUserId(userId);
        return "下单成功";
    }

    public List<OrderVO> getOrderList(Long userId) { return orderMapper.selectOrderList(userId); }
    public String shipOrder(Long orderId) { return orderMapper.updateOrderStatus(orderId, 1) > 0 ? "发货成功" : "发货失败"; }
    public String receiveOrder(Long orderId) { return orderMapper.updateOrderStatus(orderId, 2) > 0 ? "收货成功" : "收货失败"; }
}