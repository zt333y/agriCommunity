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

    // 🌟 这里修改了表名：tb_order -> t_order
    @Select("SELECT * FROM t_order ORDER BY create_time DESC")
    List<OrderVO> selectAllOrders();

    // 修改订单状态的 SQL 方法
    @org.apache.ibatis.annotations.Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateOrderStatus(@org.apache.ibatis.annotations.Param("orderId") Long orderId,
                          @org.apache.ibatis.annotations.Param("status") Integer status);
}