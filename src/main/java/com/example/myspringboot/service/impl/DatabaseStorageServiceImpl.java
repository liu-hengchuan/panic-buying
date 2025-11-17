package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import com.example.myspringboot.mapper.GoodsMapper;
import com.example.myspringboot.mapper.OrderMapper;
import com.example.myspringboot.mapper.SoldUserMapper;
import com.example.myspringboot.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Service
public class DatabaseStorageServiceImpl implements StorageService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SoldUserMapper soldUserMapper;

    // 用于存储秒杀路径
    private Map<String, String> seckillPathMap = new ConcurrentHashMap<>();

    @Override
    public void addGoods(Goods goods) {
        goodsMapper.insert(goods);
    }

    @Override
    public void batchAddGoods(List<Goods> goodsList) {
        for (Goods goods : goodsList) {
            goodsMapper.insert(goods);
        }
    }

    @Override
    public Goods getGoods(String id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public List<Goods> getAllGoods() {
        return goodsMapper.selectList(null);
    }

    @Override
    public void updateGoodsStatus(String id, int status) {
        Goods goods = new Goods();
        goods.setId(id);
        goods.setStatus(status);
        goodsMapper.updateById(goods);
    }

    @Override
    public void deleteGoods(String id) {
        goodsMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int getStock(String id) {
        Goods goods = goodsMapper.selectById(id);
        return goods != null ? goods.getAvailableStock() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(String id, int quantity) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null || goods.getAvailableStock() < quantity) {
            return false;
        }

        int updateCount = goodsMapper.update(null, Wrappers.<Goods>update()
                .eq("id", id)
                .ge("available_stock", quantity)
                .setSql("available_stock = available_stock - " + quantity));

        return updateCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(String id, int quantity) {
        goodsMapper.update(null, Wrappers.<Goods>update()
                .eq("id", id)
                .setSql("available_stock = available_stock + " + quantity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetStock(String id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods != null) {
            goodsMapper.update(null, Wrappers.<Goods>update()
                    .eq("id", id)
                    .set("available_stock", goods.getTotalStock()));
        }
    }

    @Override
    public boolean hasUserPurchased(String id, String userId) {
        return soldUserMapper.countByGoodsIdAndUserId(id, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSoldUser(String id, String userId) {
        return soldUserMapper.insert(id, userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSoldUser(String id, String userId) {
        soldUserMapper.delete(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(Order order) {
        orderMapper.insert(order);
    }

    @Override
    public Order getOrder(String orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByGoodsId(String id) {
        return orderMapper.selectByGoodsId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(String orderId, int status) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(status);
        orderMapper.updateById(order);
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
