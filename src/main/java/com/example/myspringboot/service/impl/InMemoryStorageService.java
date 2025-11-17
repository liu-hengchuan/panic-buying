package com.example.myspringboot.service.impl;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import com.example.myspringboot.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InMemoryStorageService implements StorageService {
    private Map<String, Goods> goodsMap = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();
    private Map<String, Order> orderMap = new ConcurrentHashMap<>();
    private Map<String, Set<String>> soldUsersMap = new ConcurrentHashMap<>();
    private Map<String, String> seckillPathMap = new ConcurrentHashMap<>();
    private Random random = new Random();

    @Override
    public void addGoods(Goods goods) {
        goodsMap.put(goods.getId(), goods);
        stockMap.putIfAbsent(goods.getId(), new AtomicInteger(goods.getAvailableStock()));
    }

    @Override
    public void batchAddGoods(List<Goods> goodsList) {
        for (Goods goods : goodsList) {
            addGoods(goods);
        }
    }

    @Override
    public Goods getGoods(String id) {
        return goodsMap.get(id);
    }

    @Override
    public List<Goods> getAllGoods() {
        return new ArrayList<>(goodsMap.values());
    }

    @Override
    public void updateGoodsStatus(String id, int status) {
        Goods goods = goodsMap.get(id);
        if (goods != null) {
            goods.setStatus(status);
        }
    }

    @Override
    public void deleteGoods(String id) {
        goodsMap.remove(id);
        stockMap.remove(id);
        soldUsersMap.remove(id);
    }

    @Override
    public int getStock(String id) {
        AtomicInteger stock = stockMap.get(id);
        return stock != null ? stock.get() : 0;
    }

    @Override
    public boolean deductStock(String id, int quantity) {
        AtomicInteger stock = stockMap.get(id);
        if (stock == null) {
            return false;
        }
        int currentStock = stock.get();
        if (currentStock < quantity) {
            return false;
        }
        return stock.compareAndSet(currentStock, currentStock - quantity);
    }

    @Override
    public void restoreStock(String id, int quantity) {
        AtomicInteger stock = stockMap.get(id);
        if (stock != null) {
            stock.addAndGet(quantity);
        }
    }

    @Override
    public void resetStock(String id) {
        Goods goods = goodsMap.get(id);
        if (goods != null) {
            stockMap.put(id, new AtomicInteger(goods.getAvailableStock()));
        }
    }

    @Override
    public boolean hasUserPurchased(String id, String userId) {
        Set<String> soldUsers = soldUsersMap.get(id);
        return soldUsers != null && soldUsers.contains(userId);
    }

    @Override
    public boolean addSoldUser(String id, String userId) {
        Set<String> soldUsers = soldUsersMap.computeIfAbsent(id, k -> Collections.synchronizedSet(new HashSet<>()));
        return soldUsers.add(userId);
    }

    @Override
    public void removeSoldUser(String id, String userId) {
        Set<String> soldUsers = soldUsersMap.get(id);
        if (soldUsers != null) {
            soldUsers.remove(userId);
        }
    }

    @Override
    public void createOrder(Order order) {
        orderMap.put(order.getOrderId(), order);
    }

    @Override
    public Order getOrder(String orderId) {
        return orderMap.get(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getUserId().equals(userId)) {
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByGoodsId(String id) {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getGoodsId().equals(id)) {
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(String orderId, int status) {
        Order order = orderMap.get(orderId);
        if (order != null) {
            order.setStatus(status);
        }
    }

    @Override
    public String generateSeckillPath(String id) {
        String path = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        seckillPathMap.put(id, path);
        return path;
    }

    @Override
    public boolean validateSeckillPath(String path, String id) {
        String storedPath = seckillPathMap.get(id);
        return path != null && path.equals(storedPath);
    }
}
