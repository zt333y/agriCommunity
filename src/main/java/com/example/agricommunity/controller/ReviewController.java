package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Review;
import com.example.agricommunity.entity.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private com.example.agricommunity.mapper.ReviewMapper reviewMapper;

    @PostMapping("/add")
    @Transactional
    public Result<String> addReview(@RequestBody Review review) {
        reviewMapper.insertReview(review);
        reviewMapper.markOrderReviewed(review.getOrderId());
        return Result.success("评价成功，感谢您的支持！");
    }

    @GetMapping("/list")
    public Result<List<ReviewVO>> getReviewList() {
        return Result.success(reviewMapper.selectReviewList());
    }

    // 🌟 新增：前端商品详情页调用，获取该商品下的所有评价
    @GetMapping("/product")
    public Result<List<ReviewVO>> getProductReviews(@RequestParam Long productId) {
        return Result.success(reviewMapper.selectByProductId(productId));
    }
}