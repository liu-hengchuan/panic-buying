package com.example.myspringboot.service;

import java.util.Map;

public interface AdminService {
    
    /**
     * 初始化商品数据
     */
    void initGoodsData();
    
    /**
     * 重置秒杀活动
     */
    void resetSeckill(String goodsId);
    
    /**
     * 查看秒杀统计
     */
    Map<String, Object> getSeckillStatistics(String goodsId);
}
