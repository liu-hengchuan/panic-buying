package com.example.myspringboot.service.impl;

import com.example.myspringboot.entity.Order;
import com.example.myspringboot.entity.PageResult;
import com.example.myspringboot.service.OrderService;
import com.example.myspringboot.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StorageService storageService;

    @Override
    public Order getOrderDetail(String orderId) {
        return storageService.getOrder(orderId);
    }

    @Override
    public PageResult<Order> getUserOrders(String userId, int page, int size, Integer status) {
        List<Order> allOrders = storageService.getOrdersByUserId(userId);

        // 过滤状态
        List<Order> filteredOrders = allOrders.stream()
                .filter(order -> status == null || order.getStatus() == status)
                .collect(Collectors.toList());

        // 分页处理
        int total = filteredOrders.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Order> pageList = start < end ? filteredOrders.subList(start, end) : new ArrayList<>();

        return new PageResult<>(pageList, total, page, size);
    }

    @Override
    public boolean cancelOrder(String orderId) {
        Order order = storageService.getOrder(orderId);
        if (order == null || order.getStatus() != 0) {
            // 订单不存在或已支付/完成/取消
            return false;
        }

        // 恢复库存
        storageService.restoreStock(order.getGoodsId(), order.getQuantity());

        // 移除已购买用户记录
        storageService.removeSoldUser(order.getGoodsId(), order.getUserId());

        // 更新订单状态为已取消
        storageService.updateOrderStatus(orderId, 3);
        return true;
    }
}