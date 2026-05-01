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

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public String checkout(Long userId, String address) {
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) return "购物车是空的！";

        BigDecimal total = BigDecimal.ZERO;
        for (CartVO item : cartList) {
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        Order order = new Order();

        // 🌟 核心修改：生成 16 位纯数字订单号（时间戳后 10 位 + 6 位随机数）
        String timeStr = String.valueOf(System.currentTimeMillis());
        String timePart = timeStr.substring(timeStr.length() - 10); // 截取后10位
        int randomPart = (int) ((Math.random() * 9 + 1) * 100000);  // 6位随机数
        order.setOrderNo(timePart + randomPart);

        order.setUserId(userId);
        order.setLeaderId(2L);
        order.setCommunityId(1L);
        order.setTotalAmount(total);
        order.setStatus(0);
        order.setAddress(address);

        orderMapper.insertOrder(order);

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

    public String shipOrder(Long orderId) {
        int rows = orderMapper.updateOrderStatus(orderId, 1);
        if (rows > 0) {
            return "发货成功";
        }
        return "发货失败，订单可能不存在";
    }

    public String receiveOrder(Long orderId) {
        int rows = orderMapper.updateOrderStatus(orderId, 2);
        if (rows > 0) {
            return "收货成功";
        }
        return "收货失败，订单可能不存在";
    }
}