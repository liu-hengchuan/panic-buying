package com.example.myspringboot.service.impl;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.service.AdminService;
import com.example.myspringboot.service.GoodsService;
import com.example.myspringboot.service.InMemoryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private InMemoryStorageService storageService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 初始化商品数据
     */
    @Override
    public void initGoodsData() {
        // 创建一些示例商品数据
        // 这里创建10款手机产品作为示例
        for (int i = 1; i <= 10; i++) {
            Goods goods = new Goods();
            goods.setId("100" + i);
            goods.setName("iPhone 15 Pro " + i);
            goods.setTitle("iPhone 15 Pro 256GB - 款式 " + i);
            goods.setImage("https://example.com/iphone15.jpg");
            goods.setPrice(8999.00);
            goods.setSeckillPrice(7999.00);
            goods.setTotalStock(1000);
            goods.setAvailableStock(1000);
            // 设置秒杀时间为当前时间前后
            goods.setStartTime(LocalDateTime.now().minusMinutes(30));
            goods.setEndTime(LocalDateTime.now().plusHours(2));
            goods.setStatus(1); // 进行中

            storageService.addGoods(goods);
        }
    }

    /**
     * 重置秒杀活动
     */
    @Override
    public void resetSeckill(String goodsId) {
        goodsService.resetSeckill(goodsId);
    }

    /**
     * 查看秒杀统计
     */
    @Override
    public Map<String, Object> getSeckillStatistics(String goodsId) {
        Map<String, Object> statistics = new HashMap<>();

        Goods goods = storageService.getGoods(goodsId);
        if (goods != null) {
            int totalStock = goods.getTotalStock();
            int availableStock = storageService.getStock(goodsId);
            int soldCount = totalStock - availableStock;
            double sellRate = totalStock > 0 ? (double) soldCount / totalStock * 100 : 0;

            statistics.put("goodsId", goodsId);
            statistics.put("totalStock", totalStock);
            statistics.put("availableStock", availableStock);
            statistics.put("soldCount", soldCount);
            statistics.put("sellRate", String.format("%.2f%%", sellRate));
            statistics.put("status", goods.getStatus());
        }

        return statistics;
    }
}