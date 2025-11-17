package com.example.myspringboot.service.impl;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.PageResult;
import com.example.myspringboot.service.GoodsService;

import com.example.myspringboot.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private StorageService storageService;

    @Override
    public PageResult<Goods> getSeckillGoodsList(int page, int size, Integer status) {
        List<Goods> allGoods = new ArrayList<>(storageService.getAllGoods());

        // 过滤状态
        List<Goods> filteredGoods = allGoods.stream()
                .filter(goods -> status == null || goods.getStatus() == status)
                .collect(Collectors.toList());

        // 分页处理
        int total = filteredGoods.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Goods> pageList = start < end ? filteredGoods.subList(start, end) : new ArrayList<>();

        // 更新商品状态和倒计时
        LocalDateTime now = LocalDateTime.now();
        pageList.forEach(goods -> updateGoodsStatusAndCountdown(goods, now));

        return new PageResult<>(pageList, total, page, size);
    }

    @Override
    public Goods getGoodsDetail(String goodsId) {
        Goods goods = storageService.getGoods(goodsId);
        if (goods != null) {
            updateGoodsStatusAndCountdown(goods, LocalDateTime.now());
        }
        return goods;
    }

    @Override
    public long getSeckillCountdown(String goodsId) {
        Goods goods = storageService.getGoods(goodsId);
        if (goods == null) {
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(goods.getStartTime())) {
            // 秒杀未开始，返回距离开始的时间
            return java.time.Duration.between(now, goods.getStartTime()).toMillis();
        } else if (now.isBefore(goods.getEndTime())) {
            // 秒杀进行中，返回距离结束的时间
            return java.time.Duration.between(now, goods.getEndTime()).toMillis();
        } else {
            // 秒杀已结束
            return -1;
        }
    }

    @Override
    public void initGoodsData(List<Goods> goodsList) {
        storageService.batchAddGoods(goodsList);
    }

    @Override
    public void resetSeckill(String goodsId) {
        storageService.resetStock(goodsId);
        // 重置商品状态
        Goods goods = storageService.getGoods(goodsId);
        if (goods != null) {
            LocalDateTime now = LocalDateTime.now();
            updateGoodsStatusAndCountdown(goods, now);
        }
    }

    /**
     * 更新商品状态和倒计时
     */
    private void updateGoodsStatusAndCountdown(Goods goods, LocalDateTime now) {
        if (now.isBefore(goods.getStartTime())) {
            goods.setStatus(0); // 未开始
            goods.setCountdown(java.time.Duration.between(now, goods.getStartTime()).toMillis());
        } else if (now.isBefore(goods.getEndTime())) {
            goods.setStatus(1); // 进行中
            goods.setCountdown(java.time.Duration.between(now, goods.getEndTime()).toMillis());
        } else {
            goods.setStatus(2); // 已结束
            goods.setCountdown(-1);
        }
    }
}