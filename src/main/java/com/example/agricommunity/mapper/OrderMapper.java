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
    // ================= 基础下单操作 =================
    int insertOrder(Order order);
    int insertOrderItem(OrderItem orderItem);

    // 查询订单列表（XML 实现）
    List<OrderVO> selectOrderList(Long userId);
    List<OrderVO> selectAllOrders();

    // 更新主订单状态 (兼容所有的旧版 Controller 代码)
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    // ================= 统计相关 (Web 大屏数据看板使用) =================
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM t_order WHERE status >= 1")
    BigDecimal sumTotalSales();

    @Select("SELECT COUNT(*) FROM t_order")
    int countTotalOrders();

    // 🌟 核心修复：把你大屏折线图需要的近7天统计方法补回来了！
    @Select("SELECT DATE_FORMAT(create_time, '%m-%d') as date, SUM(total_amount) as sales " +
            "FROM t_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY date ORDER BY date")
    List<Map<String, Object>> selectLastSevenDaysSales();


    // ================= 农户发货与合单逻辑 (多商户架构) =================
    @Select("SELECT i.product_id AS productId, i.product_name AS productName, " +
            "SUM(i.quantity) AS totalQuantity, p.unit AS unit " +
            "FROM t_order_item i " +
            "JOIN t_order o ON i.order_id = o.id " +
            "JOIN t_product p ON i.product_id = p.id " +
            "WHERE i.farmer_id = #{farmerId} AND o.status = 0 AND i.status = 0 " +
            "GROUP BY i.product_id, i.product_name, p.unit")
    List<FarmerPickingVO> selectPickingList(@Param("farmerId") Long farmerId);

    @Update("UPDATE t_order_item i JOIN t_order o ON i.order_id = o.id " +
            "SET i.status = 1 " +
            "WHERE i.farmer_id = #{farmerId} AND i.product_id = #{productId} AND o.status = 0 AND i.status = 0")
    int shipByProduct(@Param("farmerId") Long farmerId, @Param("productId") Long productId);

    @Select("SELECT DISTINCT i.order_id FROM t_order_item i JOIN t_order o ON i.order_id = o.id " +
            "WHERE i.farmer_id = #{farmerId} AND i.product_id = #{productId} AND o.status = 0")
    List<Long> getAffectedOrderIds(@Param("farmerId") Long farmerId, @Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM t_order_item WHERE order_id = #{orderId} AND status = 0")
    int countUnshippedItems(@Param("orderId") Long orderId);

    @Select("SELECT i.*, p.image_url as imageUrl FROM t_order_item i LEFT JOIN t_product p ON i.product_id = p.id WHERE i.order_id = #{orderId}")
    List<OrderItem> selectItemsByOrderId(@Param("orderId") Long orderId);

    // ================= 团长相关 =================
    List<OrderVO> selectOrdersByDistrict(@Param("district") String district);
}