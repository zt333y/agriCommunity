package com.example.agricommunity.controller;

import com.example.agricommunity.common.Result;
import com.example.agricommunity.entity.OrderItem;
import com.example.agricommunity.entity.OrderVO;
import com.example.agricommunity.entity.User;
import com.example.agricommunity.mapper.UserMapper;
import com.example.agricommunity.service.OrderService;
import com.example.agricommunity.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/create")
    public Result<String> createOrder(HttpServletRequest request, @RequestParam("address") String address) {
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());
        String msg = orderService.checkout(userId, address);
        return "下单成功".equals(msg) ? Result.success(msg) : Result.error(msg);
    }

    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("currentUserId").toString());
        return Result.success(orderService.getOrderList(userId));
    }

    @PostMapping("/receive")
    public Result<String> receiveOrder(Long orderId) {
        return Result.success(orderService.receiveOrder(orderId));
    }

    @GetMapping("/leaderList")
    public Result<List<OrderVO>> getLeaderOrders(HttpServletRequest request) {
        Long leaderId = Long.valueOf(request.getAttribute("currentUserId").toString());

        User leader = userMapper.selectById(leaderId);
        if (leader == null || leader.getAddress() == null) {
            return Result.error("未获取到团长地址，无法分配订单");
        }
        String leaderAddr = leader.getAddress();

        String district = "";
        if (leaderAddr.contains("市") && leaderAddr.contains("区")) {
            district = leaderAddr.substring(leaderAddr.indexOf("市") + 1, leaderAddr.indexOf("区") + 1);
        } else if (leaderAddr.contains("区")) {
            district = leaderAddr.substring(0, leaderAddr.indexOf("区") + 1);
        } else if (leaderAddr.contains("县")) {
            district = leaderAddr.substring(0, leaderAddr.indexOf("县") + 1);
        }

        if (district.isEmpty()) {
            return Result.error("您的资料未包含明确的区/县信息，无法拉取辖区订单");
        }

        List<OrderVO> areaOrders = orderMapper.selectOrdersByDistrict(district);

        return Result.success("获取本区域订单成功", areaOrders);
    }

    @PostMapping("/arrive")
    public Result<String> arriveOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 4);
        return Result.success("入库成功，已通知居民前来提货");
    }

    @PostMapping("/verify")
    public Result<String> verifyOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 2);
        return Result.success("核销成功，订单已完成流转");
    }

    @GetMapping("/pickingList")
    public Result<List<com.example.agricommunity.entity.FarmerPickingVO>> getPickingList(HttpServletRequest request) {
        Long farmerId = Long.valueOf(request.getAttribute("currentUserId").toString());
        return Result.success(orderMapper.selectPickingList(farmerId));
    }

    @PostMapping("/ship")
    public Result<String> shipOrder(Long orderId) {
        orderMapper.updateStatus(orderId, 1);
        return Result.success("发货成功，已流转至社区团长端");
    }

    // 🌟 致命 BUG 修复在这里！！
    @PostMapping("/shipByProduct")
    public Result<String> shipByProduct(Long productId, HttpServletRequest request) {
        Long farmerId = Long.valueOf(request.getAttribute("currentUserId").toString());

        // 🌟 之前直接调用了 orderMapper，越过了检查程序！
        // 🌟 现在强制调用 orderService 里带有“全部发货才扭转主订单”的高级逻辑！
        String msg = orderService.shipByProduct(farmerId, productId);

        if ("发货成功".equals(msg)) {
            return Result.success("一键发货成功，商品已流转至社区团长端！");
        }
        return Result.error(msg);
    }

    @GetMapping("/items")
    public Result<List<OrderItem>> getOrderItems(@RequestParam Long orderId) {
        return Result.success(orderMapper.selectItemsByOrderId(orderId));
    }

    @PostMapping("/applyAfterSales")
    public Result<String> applyAfterSales(Long orderId, String reason) {
        String msg = orderService.applyAfterSales(orderId, reason);
        if (msg.contains("已提交")) return Result.success(msg);
        return Result.error(msg);
    }

    @PostMapping("/approveAfterSales")
    public Result<String> approveAfterSales(Long orderId, boolean isAgree) {
        String msg = orderService.approveAfterSales(orderId, isAgree);
        return Result.success(msg);
    }

    @PostMapping("/leaderConfirmReturn")
    public Result<String> leaderConfirmReturn(Long orderId) {
        String msg = orderService.leaderConfirmReturn(orderId);
        if (msg.contains("完成")) return Result.success(msg);
        return Result.error(msg);
    }
}