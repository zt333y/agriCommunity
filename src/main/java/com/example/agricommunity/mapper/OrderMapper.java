package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper {
    // 插入主订单（注意：需要在XML里配置返回自增的主键ID）
    int insertOrder(Order order);

    // 插入订单明细
    int insertOrderItem(OrderItem orderItem);

    // 查询我的订单列表
    java.util.List<com.example.agricommunity.entity.OrderVO> selectOrderList(Long userId);

    // 查询全平台所有订单
    @Select("SELECT * FROM tb_order ORDER BY create_time DESC")
    List<OrderVO> selectAllOrders();
}