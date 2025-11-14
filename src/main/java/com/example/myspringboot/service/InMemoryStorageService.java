package com.example.myspringboot.service;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class InMemoryStorageService {
    // 商品信息存储: seckill:goods:{goodsId}
    private final ConcurrentHashMap<String, Goods> goodsMap = new ConcurrentHashMap<>();
    
    // 库存存储: seckill:stock:{goodsId} -> AtomicInteger
    private final ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();
    
    // 已购买用户存储: seckill:sold:{goodsId} -> Set<String> userId
    private final ConcurrentHashMap<String, Set<String>> soldUsersMap = new ConcurrentHashMap<>();
    
    // 订单信息存储: seckill:order:{orderId} -> Order
    private final ConcurrentHashMap<String, Order> orderMap = new ConcurrentHashMap<>();
    
    // 用户订单列表: userId -> List<Order>
    private final ConcurrentHashMap<String, List<Order>> userOrdersMap = new ConcurrentHashMap<>();
    
    // 秒杀路径存储: path -> goodsId, 并设置过期时间
    private final ConcurrentHashMap<String, PathInfo> pathMap = new ConcurrentHashMap<>();
    
    // 路径信息内部类
    private static class PathInfo {
        private String goodsId;
        private LocalDateTime expireTime;
        
        public PathInfo(String goodsId, LocalDateTime expireTime) {
            this.goodsId = goodsId;
            this.expireTime = expireTime;
        }
        
        public String getGoodsId() {
            return goodsId;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
    }
    
    // ------------------- 商品相关方法 -------------------
    
    /**
     * 添加商品
     */
    public void addGoods(Goods goods) {
        goodsMap.put(goods.getId(), goods);
        stockMap.put(goods.getId(), new AtomicInteger(goods.getAvailableStock()));
        soldUsersMap.putIfAbsent(goods.getId(), Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }
    
    /**
     * 批量添加商品
     */
    public void batchAddGoods(List<Goods> goodsList) {
        goodsList.forEach(this::addGoods);
    }
    
    /**
     * 获取商品
     */
    public Goods getGoods(String goodsId) {
        return goodsMap.get(goodsId);
    }
    
    /**
     * 获取所有商品
     */
    public Collection<Goods> getAllGoods() {
        return goodsMap.values();
    }
    
    /**
     * 更新商品状态
     */
    public void updateGoodsStatus(String goodsId, int status) {
        Goods goods = goodsMap.get(goodsId);
        if (goods != null) {
            goods.setStatus(status);
        }
    }
    
    // ------------------- 库存相关方法 -------------------
    
    /**
     * 获取库存
     */
    public int getStock(String goodsId) {
        AtomicInteger stock = stockMap.get(goodsId);
        return stock != null ? stock.get() : 0;
    }
    
    /**
     * 扣减库存 (原子操作)
     */
    public boolean deductStock(String goodsId, int quantity) {
        AtomicInteger stock = stockMap.get(goodsId);
        if (stock == null) {
            return false;
        }
        
        int current;
        int next;
        do {
            current = stock.get();
            if (current < quantity) {
                return false;
            }
            next = current - quantity;
        } while (!stock.compareAndSet(current, next));
        
        // 更新商品的可用库存
        Goods goods = goodsMap.get(goodsId);
        if (goods != null) {
            goods.setAvailableStock(next);
        }
        
        return true;
    }
    
    /**
     * 恢复库存
     */
    public boolean restoreStock(String goodsId, int quantity) {
        AtomicInteger stock = stockMap.get(goodsId);
        if (stock == null) {
            return false;
        }
        
        stock.addAndGet(quantity);
        
        // 更新商品的可用库存
        Goods goods = goodsMap.get(goodsId);
        if (goods != null) {
            goods.setAvailableStock(goods.getAvailableStock() + quantity);
        }
        
        return true;
    }
    
    /**
     * 重置库存
     */
    public void resetStock(String goodsId) {
        Goods goods = goodsMap.get(goodsId);
        if (goods != null) {
            stockMap.put(goodsId, new AtomicInteger(goods.getTotalStock()));
            goods.setAvailableStock(goods.getTotalStock());
        }
    }
    
    // ------------------- 已购买用户相关方法 -------------------
    
    /**
     * 检查用户是否已购买
     */
    public boolean hasUserPurchased(String goodsId, String userId) {
        Set<String> soldUsers = soldUsersMap.get(goodsId);
        return soldUsers != null && soldUsers.contains(userId);
    }
    
    /**
     * 添加已购买用户
     */
    public boolean addSoldUser(String goodsId, String userId) {
        Set<String> soldUsers = soldUsersMap.get(goodsId);
        if (soldUsers == null) {
            return false;
        }
        return soldUsers.add(userId);
    }
    
    /**
     * 移除已购买用户
     */
    public boolean removeSoldUser(String goodsId, String userId) {
        Set<String> soldUsers = soldUsersMap.get(goodsId);
        if (soldUsers == null) {
            return false;
        }
        return soldUsers.remove(userId);
    }
    
    // ------------------- 订单相关方法 -------------------
    
    /**
     * 创建订单
     */
    public void createOrder(Order order) {
        orderMap.put(order.getOrderId(), order);
        
        // 添加到用户订单列表
        userOrdersMap.computeIfAbsent(order.getUserId(), k -> new ArrayList<>())
                    .add(order);
    }
    
    /**
     * 获取订单
     */
    public Order getOrder(String orderId) {
        return orderMap.get(orderId);
    }
    
    /**
     * 获取用户订单列表
     */
    public List<Order> getUserOrders(String userId) {
        return userOrdersMap.getOrDefault(userId, Collections.emptyList());
    }
    
    /**
     * 更新订单状态
     */
    public boolean updateOrderStatus(String orderId, int status) {
        Order order = orderMap.get(orderId);
        if (order != null) {
            order.setStatus(status);
            return true;
        }
        return false;
    }
    
    /**
     * 删除订单
     */
    public boolean deleteOrder(String orderId) {
        Order order = orderMap.remove(orderId);
        if (order != null) {
            // 从用户订单列表中移除
            List<Order> userOrders = userOrdersMap.get(order.getUserId());
            if (userOrders != null) {
                userOrders.removeIf(o -> o.getOrderId().equals(orderId));
            }
            return true;
        }
        return false;
    }
    
    // ------------------- 秒杀路径相关方法 -------------------
    
    /**
     * 生成秒杀路径
     */
    public String generateSeckillPath(String goodsId) {
        String path = UUID.randomUUID().toString().replace("-", "");
        // 设置5分钟过期时间
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);
        pathMap.put(path, new PathInfo(goodsId, expireTime));
        return path;
    }
    
    /**
     * 验证秒杀路径
     */
    public boolean validateSeckillPath(String path, String goodsId) {
        PathInfo pathInfo = pathMap.get(path);
        if (pathInfo == null) {
            return false;
        }
        if (pathInfo.isExpired()) {
            pathMap.remove(path);
            return false;
        }
        return pathInfo.getGoodsId().equals(goodsId);
    }
    
    // ------------------- 统计相关方法 -------------------
    
    /**
     * 获取已售出数量
     */
    public int getSoldCount(String goodsId) {
        Goods goods = goodsMap.get(goodsId);
        if (goods == null) {
            return 0;
        }
        return goods.getTotalStock() - getStock(goodsId);
    }
    
    /**
     * 清理过期路径
     */
    public void cleanupExpiredPaths() {
        LocalDateTime now = LocalDateTime.now();
        pathMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}