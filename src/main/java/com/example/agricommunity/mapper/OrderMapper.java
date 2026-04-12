package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM t_order WHERE status >= 1")
    BigDecimal sumTotalSales();

    @Select("SELECT COUNT(*) FROM t_order")
    int countTotalOrders();

    // 统计近七天每天的销售额 (MySQL 语法)
    @Select("SELECT DATE_FORMAT(create_time, '%m-%d') as date, SUM(total_amount) as sales " +
            "FROM t_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> selectLastSevenDaysSales();

    // 🌟 获取所有订单 (团长需要查看流转到社区的订单)
    @Select("SELECT * FROM t_order ORDER BY create_time DESC")
    List<OrderVO> selectAllOrders();

    // 🌟 通用更新订单状态方法
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateStatus(Long orderId, Integer status);
}