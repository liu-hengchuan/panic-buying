package com.example.myspringboot.service;

import com.example.myspringboot.entity.Order;
import com.example.myspringboot.entity.PageResult;

public interface OrderService {

    /**
     * 获取订单详情
     */
    Order getOrderDetail(String orderId);

    /**
     * 获取用户订单列表
     */
    PageResult<Order> getUserOrders(String userId, int page, int size, Integer status);

    /**
     * 取消订单并恢复库存
     */
    boolean cancelOrder(String orderId);
}