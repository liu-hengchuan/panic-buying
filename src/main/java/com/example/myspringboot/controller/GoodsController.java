package com.example.myspringboot.controller;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.PageResult;
import com.example.myspringboot.entity.Response;
import com.example.myspringboot.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill/goods")
public class GoodsController {
    
    @Autowired
    private GoodsService goodsService;
    
    /**
     * 获取秒杀商品列表
     */
    @GetMapping("/list")
    public Response<PageResult<Goods>> getSeckillGoodsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status) {
        PageResult<Goods> result = goodsService.getSeckillGoodsList(page, size, status);
        return Response.success(result);
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/detail/{goodsId}")
    public Response<Goods> getGoodsDetail(@PathVariable String goodsId) {
        Goods goods = goodsService.getGoodsDetail(goodsId);
        if (goods == null) {
            return Response.error(404, "商品不存在");
        }
        return Response.success(goods);
    }
    
    /**
     * 获取秒杀倒计时
     */
    @GetMapping("/countdown/{goodsId}")
    public Response<Map<String, Object>> getSeckillCountdown(@PathVariable String goodsId) {
        long countdown = goodsService.getSeckillCountdown(goodsId);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("countdown", countdown);
        return Response.success(data);
    }
}
