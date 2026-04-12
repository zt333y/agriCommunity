package com.example.agricommunity.mapper;

import com.example.agricommunity.entity.Review;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ReviewMapper {
    @Insert("INSERT INTO t_review(user_id, order_id, product_id, content, score) " +
            "VALUES(#{userId}, #{orderId}, #{productId}, #{content}, #{score})")
    int insertReview(Review review);

    // 🌟 亮点：更新订单状态为 3，表示“已评价”
    @Update("UPDATE t_order SET status = 3 WHERE id = #{orderId}")
    int markOrderReviewed(Long orderId);
}