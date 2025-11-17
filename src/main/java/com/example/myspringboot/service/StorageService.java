package com.example.myspringboot.service;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;

import java.util.List;

public interface StorageService {
    // ------------------- 商品相关方法 -------------------
    void addGoods(Goods goods);
    void batchAddGoods(List<Goods> goodsList);
    Goods getGoods(String id);
    List<Goods> getAllGoods();
    void updateGoodsStatus(String id, int status);
    void deleteGoods(String id);

    // ------------------- 库存相关方法 -------------------
    int getStock(String id);
    boolean deductStock(String id, int quantity);
    void restoreStock(String id, int quantity);
    void resetStock(String id);

    // ------------------- 已购买用户相关方法 -------------------
    boolean hasUserPurchased(String id, String userId);
    boolean addSoldUser(String id, String userId);
    void removeSoldUser(String id, String userId);

    // ------------------- 订单相关方法 -------------------
    void createOrder(Order order);
    Order getOrder(String orderId);
    List<Order> getOrdersByUserId(String userId);
    List<Order> getOrdersByGoodsId(String id);
    void updateOrderStatus(String orderId, int status);

    // ------------------- 秒杀路径相关方法 -------------------
    String generateSeckillPath(String id);
    boolean validateSeckillPath(String path, String id);
}