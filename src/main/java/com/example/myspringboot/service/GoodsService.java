package com.example.myspringboot.service;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.PageResult;

import java.util.List;

public interface GoodsService {

    /**
     * 获取秒杀商品列表
     */
    PageResult<Goods> getSeckillGoodsList(int page, int size, Integer status);

    /**
     * 获取商品详情
     */
    Goods getGoodsDetail(String goodsId);

    /**
     * 获取秒杀倒计时
     */
    long getSeckillCountdown(String goodsId);

    /**
     * 批量初始化商品数据
     */
    void initGoodsData(List<Goods> goodsList);

    /**
     * 重置秒杀活动
     */
    void resetSeckill(String goodsId);
}