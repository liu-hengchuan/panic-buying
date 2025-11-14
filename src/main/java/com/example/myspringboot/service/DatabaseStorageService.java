package com.example.myspringboot.service;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DatabaseStorageService implements StorageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 缓存秒杀路径，使用ConcurrentHashMap保证线程安全
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

    @Override
    public void addGoods(Goods goods) {
        String sql = "INSERT OR REPLACE INTO goods (id, name, title, image, price, seckill_price, total_stock, available_stock, start_time, end_time, status, countdown) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, goods.getId(), goods.getName(), goods.getTitle(), goods.getImage(), goods.getPrice(), goods.getSeckillPrice(), goods.getTotalStock(), goods.getAvailableStock(), goods.getStartTime(), goods.getEndTime(), goods.getStatus(), goods.getCountdown());
    }

    @Override
    public void batchAddGoods(List<Goods> goodsList) {
        String sql = "INSERT OR REPLACE INTO goods (id, name, title, image, price, seckill_price, total_stock, available_stock, start_time, end_time, status, countdown) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, goodsList, 100, (ps, goods) -> {
            ps.setString(1, goods.getId());
            ps.setString(2, goods.getName());
            ps.setString(3, goods.getTitle());
            ps.setString(4, goods.getImage());
            ps.setDouble(5, goods.getPrice());
            ps.setDouble(6, goods.getSeckillPrice());
            ps.setInt(7, goods.getTotalStock());
            ps.setInt(8, goods.getAvailableStock());
            ps.setObject(9, goods.getStartTime());
            ps.setObject(10, goods.getEndTime());
            ps.setInt(11, goods.getStatus());
            ps.setLong(12, goods.getCountdown());
        });
    }

    @Override
    public Goods getGoods(String id) {
        String sql = "SELECT * FROM goods WHERE id = ?";
        List<Goods> goodsList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Goods.class), id);
        return goodsList.isEmpty() ? null : goodsList.get(0);
    }

    @Override
    public List<Goods> getAllGoods() {
        String sql = "SELECT * FROM goods";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Goods.class));
    }

    @Override
    public void updateGoodsStatus(String id, int status) {
        String sql = "UPDATE goods SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    @Override
    public int getStock(String id) {
        String sql = "SELECT available_stock FROM goods WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(String id, int quantity) {
        // 使用乐观锁更新库存
        String sql = "UPDATE goods SET available_stock = available_stock - ? WHERE id = ? AND available_stock >= ?";
        int updated = jdbcTemplate.update(sql, quantity, id, quantity);
        return updated > 0;
    }

    @Override
    public void restoreStock(String id, int quantity) {
        String sql = "UPDATE goods SET available_stock = available_stock + ? WHERE id = ?";
        jdbcTemplate.update(sql, quantity, id);
    }

    @Override
    public void resetStock(String id) {
        String sql = "UPDATE goods SET available_stock = total_stock WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean hasUserPurchased(String id, String userId) {
        String sql = "SELECT COUNT(*) FROM sold_users WHERE goods_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, userId);
        return count != null && count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSoldUser(String id, String userId) {
        try {
            String sql = "INSERT INTO sold_users (goods_id, user_id) VALUES (?, ?)";
            int inserted = jdbcTemplate.update(sql, id, userId);
            return inserted > 0;
        } catch (Exception e) {
            // 如果插入失败，可能是因为唯一约束冲突
            return false;
        }
    }

    @Override
    public void removeSoldUser(String id, String userId) {
        String sql = "DELETE FROM sold_users WHERE goods_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void createOrder(Order order) {
        String sql = "INSERT INTO orders (order_id, user_id, goods_id, quantity, price, status, create_time, pay_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getOrderId(), order.getUserId(), order.getGoodsId(), order.getQuantity(), order.getPrice(), order.getStatus(), order.getCreateTime(), order.getPayTime());
    }

    @Override
    public Order getOrder(String orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        List<Order> orderList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), orderId);
        return orderList.isEmpty() ? null : orderList.get(0);
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), userId);
    }

    @Override
    public List<Order> getOrdersByGoodsId(String id) {
        String sql = "SELECT * FROM orders WHERE goods_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), id);
    }

    @Override
    public void updateOrderStatus(String orderId, int status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    @Override
    public String generateSeckillPath(String id) {
        String path = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(60); // 默认60秒过期
        pathMap.put(path, new PathInfo(id, expireTime));
        return path;
    }

    @Override
    public boolean validateSeckillPath(String path, String goodsId) {
        PathInfo pathInfo = pathMap.get(path);
        if (pathInfo == null || pathInfo.isExpired()) {
            pathMap.remove(path);
            return false;
        }
        return pathInfo.getGoodsId().equals(goodsId);
    }
}