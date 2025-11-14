package com.example.myspringboot.service.impl;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import com.example.myspringboot.entity.SeckillResult;
import com.example.myspringboot.service.GoodsService;
import com.example.myspringboot.service.InMemoryStorageService;
import com.example.myspringboot.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private InMemoryStorageService storageService;

    @Autowired
    private GoodsService goodsService;

    @Override
    public String generateSeckillPath(String goodsId, String verifyCode) {
        // TODO: 实际项目中需要验证图形验证码正确性
        // 这里简化处理，直接生成路径
        return storageService.generateSeckillPath(goodsId);
    }

    @Override
    public SeckillResult executeSeckill(String path, String goodsId, String userId) {
        // 验证秒杀路径
        if (!storageService.validateSeckillPath(path, goodsId)) {
            return createResult(0, null, "无效的秒杀路径");
        }

        // 检查用户是否已购买
        if (storageService.hasUserPurchased(goodsId, userId)) {
            return createResult(0, null, "用户已购买该商品");
        }

        // 获取商品信息
        Goods goods = storageService.getGoods(goodsId);
        if (goods == null) {
            return createResult(0, null, "商品不存在");
        }

        // 检查秒杀状态
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(goods.getStartTime())) {
            return createResult(0, null, "秒杀未开始");
        }
        if (now.isAfter(goods.getEndTime())) {
            return createResult(0, null, "秒杀已结束");
        }
        if (goods.getStatus() != 1) {
            return createResult(0, null, "秒杀活动已结束");
        }

        // 扣减库存
        boolean stockDeducted = storageService.deductStock(goodsId, 1);
        if (!stockDeducted) {
            return createResult(0, null, "库存不足");
        }

        // 添加已购买用户
        boolean userAdded = storageService.addSoldUser(goodsId, userId);
        if (!userAdded) {
            // 回滚库存
            storageService.restoreStock(goodsId, 1);
            return createResult(0, null, "用户已购买该商品");
        }

        // 生成订单
        Order order = createSeckillOrder(goodsId, userId, goods.getSeckillPrice());
        storageService.createOrder(order);

        // TODO: 发送支付通知（可以使用消息队列异步处理）

        return createResult(1, order.getOrderId(), "秒杀成功");
    }

    @Override
    public SeckillResult getSeckillResult(String goodsId, String userId) {
        // 检查用户是否已购买
        if (storageService.hasUserPurchased(goodsId, userId)) {
            // 查询订单
            // 这里简化处理，实际项目中需要根据userId和goodsId查询订单
            return createResult(1, null, "秒杀成功");
        }

        // 检查库存
        if (storageService.getStock(goodsId) <= 0) {
            return createResult(0, null, "库存不足");
        }

        // 检查秒杀状态
        Goods goods = storageService.getGoods(goodsId);
        if (goods != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(goods.getStartTime())) {
                return createResult(0, null, "秒杀未开始");
            }
            if (now.isAfter(goods.getEndTime())) {
                return createResult(0, null, "秒杀已结束");
            }
        }

        return createResult(2, null, "处理中");
    }

    /**
     * 创建秒杀订单
     */
    private Order createSeckillOrder(String goodsId, String userId, double seckillPrice) {
        Order order = new Order();
        // 生成唯一订单号
        String orderId = "ORDER" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + UUID.randomUUID().toString().substring(0, 8);
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setQuantity(1);
        order.setPrice(seckillPrice);
        order.setStatus(0); // 0-创建
        order.setCreateTime(LocalDateTime.now());
        return order;
    }

    /**
     * 创建秒杀结果
     */
    private SeckillResult createResult(int result, String orderId, String reason) {
        SeckillResult seckillResult = new SeckillResult();
        seckillResult.setResult(result);
        seckillResult.setOrderId(orderId);
        seckillResult.setReason(reason);
        return seckillResult;
    }
}