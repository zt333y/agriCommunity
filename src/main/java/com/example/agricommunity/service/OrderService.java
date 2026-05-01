package com.example.agricommunity.service;

import com.example.agricommunity.entity.CartVO;
import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.mapper.CartMapper;
import com.example.agricommunity.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired private CartMapper cartMapper;
    @Autowired private OrderMapper orderMapper;

    /**
     * 用户下单逻辑 (将购物车商品合并为一个主订单)
     */
    @Transactional
    public String checkout(Long userId, String address) {
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) return "购物车是空的！";

        Order order = new Order();
        String timeStr = String.valueOf(System.currentTimeMillis());
        order.setOrderNo(timeStr.substring(timeStr.length() - 10) + (int)((Math.random() * 9 + 1) * 100));
        order.setUserId(userId);
        order.setLeaderId(2L);
        order.setCommunityId(1L);
        order.setStatus(0);
        order.setAddress(address);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartVO item : cartList) {
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        order.setTotalAmount(totalAmount);

        orderMapper.insertOrder(order);

        for (CartVO cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setFarmerId(cartItem.getFarmerId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            // 默认 status 为 0，数据库已设置
            orderMapper.insertOrderItem(orderItem);
        }

        cartMapper.deleteCartByUserId(userId);
        return "下单成功";
    }

    /**
     * 农户发货逻辑 (某个农户发了某个商品)
     */
    @Transactional
    public String shipByProduct(Long farmerId, Long productId) {
        List<Long> affectedOrderIds = orderMapper.getAffectedOrderIds(farmerId, productId);
        int count = orderMapper.shipByProduct(farmerId, productId);
        if (count == 0) return "无可发货项目";

        for (Long orderId : affectedOrderIds) {
            int unshippedCount = orderMapper.countUnshippedItems(orderId);
            if (unshippedCount == 0) {
                orderMapper.updateOrderStatus(orderId, 1);
            }
        }
        return "发货成功";
    }

    /**
     * 🌟 核心修复：补回给 Web 管理员端使用的一键发货特权方法
     */
    public String shipOrder(Long orderId) {
        return orderMapper.updateOrderStatus(orderId, 1) > 0 ? "发货成功" : "发货失败";
    }

    /**
     * 基础查询与收货逻辑
     */
    public List<OrderVO> getOrderList(Long userId) {
        return orderMapper.selectOrderList(userId);
    }

    public String receiveOrder(Long orderId) {
        return orderMapper.updateOrderStatus(orderId, 2) > 0 ? "收货成功" : "收货失败";
    }
}