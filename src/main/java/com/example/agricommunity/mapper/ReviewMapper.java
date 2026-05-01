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

    @Update("UPDATE t_order SET status = 3 WHERE id = #{orderId}")
    int markOrderReviewed(Long orderId);

    @Select("SELECT r.id, u.username, p.name as productName, r.content, r.score, r.create_time " +
            "FROM t_review r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN t_product p ON r.product_id = p.id " +
            "ORDER BY r.create_time DESC")
    List<ReviewVO> selectReviewList();

    // 🌟 核心修复：去掉了 as rating 和 as userName
    // 强制直接查出 score 和 username，确保后端实体类能成功拿到数据！
    @Select("SELECT r.id, u.username, r.content, r.score, r.create_time as createTime " +
            "FROM t_review r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} ORDER BY r.create_time DESC")
    List<ReviewVO> selectByProductId(@Param("productId") Long productId);
}