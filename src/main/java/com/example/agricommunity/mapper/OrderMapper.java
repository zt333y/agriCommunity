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

    // 居民端：查询订单 (实现在 XML)
    List<OrderVO> selectOrderList(Long userId);

    // 🌟 修复：删除这里的 @Select 注解，让它去读取 XML 里的 resultMap，这样后台大屏才能看到商品明细！
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

    @Update("UPDATE t_order SET status = #{status} WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

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

    @Update("UPDATE t_order o JOIN t_order_item i ON o.id = i.order_id " +
            "SET o.status = 1 " +
            "WHERE i.farmer_id = #{farmerId} AND i.product_id = #{productId} AND o.status = 0")
    int shipByProduct(@Param("farmerId") Long farmerId, @Param("productId") Long productId);

    @Select("SELECT i.*, p.image_url as imageUrl FROM t_order_item i LEFT JOIN t_product p ON i.product_id = p.id WHERE i.order_id = #{orderId}")
    List<OrderItem> selectItemsByOrderId(@Param("orderId") Long orderId);

    // 团长专属：根据区/县模糊查询订单 (实现在 XML)
    List<OrderVO> selectOrdersByDistrict(@Param("district") String district);
}