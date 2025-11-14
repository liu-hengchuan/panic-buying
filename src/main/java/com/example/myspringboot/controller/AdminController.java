package com.example.myspringboot.controller;

import com.example.myspringboot.entity.Response;
import com.example.myspringboot.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 初始化商品数据
     */
    @PostMapping("/goods/init")
    public Response<Void> initGoodsData() {
        adminService.initGoodsData();
        return Response.success();
    }
    
    /**
     * 重置秒杀活动
     */
    @PostMapping("/seckill/reset/{goodsId}")
    public Response<Void> resetSeckill(@PathVariable String goodsId) {
        adminService.resetSeckill(goodsId);
        return Response.success();
    }
    
    /**
     * 查看秒杀统计
     */
    @GetMapping("/statistics/{goodsId}")
    public Response<java.util.Map<String, Object>> getSeckillStatistics(@PathVariable String goodsId) {
        java.util.Map<String, Object> statistics = adminService.getSeckillStatistics(goodsId);
        return Response.success(statistics);
    }
}
