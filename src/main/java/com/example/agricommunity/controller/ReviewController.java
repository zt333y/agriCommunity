package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}