package com.example.agricommunity.service;

import com.example.agricommunity.entity.CartVO;
import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.User;
import com.example.agricommunity.mapper.CartMapper;
import com.example.agricommunity.mapper.OrderMapper;
import com.example.agricommunity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date; // 🌟 新增导入 Date
import java.util.List;

@Service
public class OrderService {

    @Autowired private CartMapper cartMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private UserMapper userMapper;

    /**
     * 用户下单逻辑
     */
    @Transactional
    public String checkout(Long userId, String address) {
        List<CartVO> cartList = cartMapper.selectCartList(userId);
        if (cartList == null || cartList.isEmpty()) return "购物车是空的！";

        Order order = new Order();
        String timeStr = String.valueOf(System.currentTimeMillis());
        order.setOrderNo(timeStr.substring(timeStr.length() - 10) + (int)((Math.random() * 9 + 1) * 100));
        order.setUserId(userId);
        order.setLeaderId(2L); // 这里的团长ID目前是写死的 2L
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
            orderMapper.insertOrderItem(orderItem);
        }

        cartMapper.deleteCartByUserId(userId);
        return "下单成功";
    }

    /**
     * 农户发货逻辑
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
     * 管理员一键发货
     */
    public String shipOrder(Long orderId) {
        return orderMapper.updateOrderStatus(orderId, 1) > 0 ? "发货成功" : "发货失败";
    }

    /**
     * 基础查询
     */
    public List<OrderVO> getOrderList(Long userId) {
        List<OrderVO> voList = orderMapper.selectOrderList(userId);

        if (voList != null) {
            for (OrderVO vo : voList) {
                // 如果订单状态为 4 (已到货)
                if (vo.getStatus() != null && vo.getStatus() == 4) {
                    Long leaderId = vo.getLeaderId();
                    if (leaderId != null) {
                        User leader = userMapper.selectById(leaderId);
                        if (leader != null) {
                            vo.setLeaderName(leader.getUsername());
                            vo.setLeaderPhone(leader.getPhone());
                            vo.setPickupAddress(leader.getAddress());
                        }
                    }
                }
            }
        }
        return voList;
    }

    // ==========================================
    // 🌟🌟🌟 下面是新增和修改的【售后功能核心逻辑】 🌟🌟🌟
    // ==========================================

    /**
     * 用户确认收货 (改造：必须记录收货时间)
     */
    public String receiveOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) return "订单不存在";

        order.setStatus(2); // 2: 已收货
        order.setReceiveTime(new Date()); // 记录当前系统时间作为收货时间

        return orderMapper.updateById(order) > 0 ? "收货成功" : "收货失败";
    }

    /**
     * 1. 用户申请售后 (核心：24小时超时校验)
     */
    public String applyAfterSales(Long orderId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) return "订单不存在";
        if (order.getStatus() != 2) return "该订单状态无法申请售后";
        if (order.getReceiveTime() == null) return "订单未记录收货时间，无法判定是否超时";

        // 核心计算：判断当前时间与收货时间的差值是否大于 24 小时
        long currentTime = System.currentTimeMillis();
        long receiveTime = order.getReceiveTime().getTime();
        if ((currentTime - receiveTime) > (24 * 60 * 60 * 1000)) {
            return "已超过提货时间24小时，无法申请售后！";
        }

        order.setStatus(5); // 5: 申请售后待审核
        order.setRefundReason(reason);
        orderMapper.updateById(order);

        return "售后申请已提交，请等待管理员审核";
    }

    /**
     * 2. Web 管理员审核售后
     */
    public String approveAfterSales(Long orderId, boolean isAgree) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 5) return "订单状态异常，无法审核";

        if (isAgree) {
            order.setStatus(6); // 6: 同意退货，等待用户退给团长
            orderMapper.updateById(order);
            return "已同意售后，等待用户退货给团长";
        } else {
            order.setStatus(8); // 8: 拒绝售后
            orderMapper.updateById(order);
            return "已拒绝该售后申请";
        }
    }

    /**
     * 3. 团长确认收到退货商品
     */
    public String leaderConfirmReturn(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 6) return "订单状态异常，无法确认收货";

        order.setStatus(7); // 7: 团长已收退货，售后完成
        orderMapper.updateById(order);

        return "确认收货成功，售后流程完成";
    }
}