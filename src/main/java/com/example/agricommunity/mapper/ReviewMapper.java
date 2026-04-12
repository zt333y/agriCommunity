package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Review;
import com.example.agricommunity.entity.ReviewVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReviewMapper {
    @Insert("INSERT INTO t_review(user_id, order_id, product_id, content, score) " +
            "VALUES(#{userId}, #{orderId}, #{productId}, #{content}, #{score})")
    int insertReview(Review review);

    // 🌟 亮点：更新订单状态为 3，表示“已评价”
    @Update("UPDATE t_order SET status = 3 WHERE id = #{orderId}")
    int markOrderReviewed(Long orderId);

    @Select("SELECT r.id, u.username, p.name as productName, r.content, r.score, r.create_time " +
            "FROM t_review r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN t_product p ON r.product_id = p.id " +
            "ORDER BY r.create_time DESC")
    List<ReviewVO> selectReviewList();
}