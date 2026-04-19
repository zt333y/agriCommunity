package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.FarmerPickingVO;
import com.example.agricommunity.entity.Order;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    // 基础下单逻辑
    int insertOrder(Order order);
    int insertOrderItem(OrderItem orderItem);
    List<OrderVO> selectOrderList(Long userId);

    // 🌟 后台管理与统计逻辑
    @Select("SELECT * FROM t_order ORDER BY create_time DESC")
    List<OrderVO> selectAllOrders();

    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM t_order WHERE status >= 1")
    BigDecimal sumTotalSales();

    @Select("SELECT COUNT(*) FROM t_order")
    int countTotalOrders();

    @Select("SELECT DATE_FORMAT(create_time, '%m-%d') as date, SUM(total_amount) as sales " +
            "FROM t_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> selectLastSevenDaysSales();

    // 🌟 核心：为社区团长提供的通用更新方法
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    // 🌟 新增：农户高阶聚合查询 - 采摘发货清单汇总
    // 逻辑：关联订单明细表、主订单表、商品表，筛选待发货(status=0)的订单，并按商品分组求和
    @Select("SELECT i.product_id AS productId, " +
            "       i.product_name AS productName, " +
            "       SUM(i.quantity) AS totalQuantity, " +
            "       p.unit AS unit " +
            "FROM t_order_item i " +
            "JOIN t_order o ON i.order_id = o.id " +
            "JOIN t_product p ON i.product_id = p.id " +
            "WHERE i.farmer_id = #{farmerId} AND o.status = 0 " +
            "GROUP BY i.product_id, i.product_name, p.unit")
    List<FarmerPickingVO> selectPickingList(@Param("farmerId") Long farmerId);

    // 🌟 新增：农户专属功能 - 按商品一键发货
    // 逻辑：将当前农户名下，包含指定商品且处于待发货(0)状态的订单，全部改为已发货(1)
    @Update("UPDATE t_order o JOIN t_order_item i ON o.id = i.order_id " +
            "SET o.status = 1 " +
            "WHERE i.farmer_id = #{farmerId} AND i.product_id = #{productId} AND o.status = 0")
    int shipByProduct(@Param("farmerId") Long farmerId, @Param("productId") Long productId);

    // 🌟 新增：根据订单ID查询这个订单里到底买了哪些商品明细
    @Select("SELECT * FROM t_order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectItemsByOrderId(@Param("orderId") Long orderId);
}