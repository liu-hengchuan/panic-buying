package com.example.myspringboot.service;

import com.example.myspringboot.entity.SeckillResult;

public interface SeckillService {

    /**
     * 生成秒杀路径
     */
    String generateSeckillPath(String goodsId, String verifyCode);

    /**
     * 执行秒杀
     */
    SeckillResult executeSeckill(String path, String goodsId, String userId);

    /**
     * 获取秒杀结果
     */
    SeckillResult getSeckillResult(String goodsId, String userId);
}