package com.example.myspringboot.controller;

import com.example.myspringboot.entity.Order;
import com.example.myspringboot.entity.PageResult;
import com.example.myspringboot.entity.Response;
import com.example.myspringboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public Response<Order> getOrderDetail(@PathVariable String orderId) {
        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Response.error(404, "订单不存在");
        }
        return Response.success(order);
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Response<PageResult<Order>> getUserOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status) {
        // TODO: 实际项目中需要从请求中获取真实的userId
        String userId = "test_user_" + System.currentTimeMillis();
        
        PageResult<Order> result = orderService.getUserOrders(userId, page, size, status);
        return Response.success(result);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Response<Void> cancelOrder(@PathVariable String orderId) {
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            return Response.success();
        } else {
            return Response.error("取消订单失败");
        }
    }
}
