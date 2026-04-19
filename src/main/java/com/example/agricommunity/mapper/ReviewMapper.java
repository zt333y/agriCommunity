package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Review;
import com.example.agricommunity.entity.ReviewVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("INSERT INTO t_review(user_id, order_id, product_id, content, score) " +
            "VALUES(#{userId}, #{orderId}, #{productId}, #{content}, #{score})")
    int insertReview(Review review);

    // 🌟 亮点：更新订单状态为 3，表示“已评价” (注意：如果你系统的"已完成"是3，这里可以改成4表示"已评价")
    @Update("UPDATE t_order SET status = 3 WHERE id = #{orderId}")
    int markOrderReviewed(Long orderId);

    // 原本提供给 Web 后端管理系统的全量查询
    @Select("SELECT r.id, u.username, p.name as productName, r.content, r.score, r.create_time " +
            "FROM t_review r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN t_product p ON r.product_id = p.id " +
            "ORDER BY r.create_time DESC")
    List<ReviewVO> selectReviewList();

    // ========================================================================
    // 🌟 新增：专门提供给 Android 商品详情页的接口
    // 这里用了 as userName 和 as rating，完美匹配 Android 端的 JSON 接收格式
    // ========================================================================
    @Select("SELECT r.id, u.username as userName, r.content, r.score as rating, r.create_time as createTime " +
            "FROM t_review r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} ORDER BY r.create_time DESC")
    List<ReviewVO> selectByProductId(@Param("productId") Long productId);
}