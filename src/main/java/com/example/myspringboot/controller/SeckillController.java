package com.example.myspringboot.controller;

import com.example.myspringboot.entity.Response;
import com.example.myspringboot.entity.SeckillResult;
import com.example.myspringboot.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {
    
    @Autowired
    private SeckillService seckillService;
    
    /**
     * 获取秒杀路径
     */
    @PostMapping("/path/{goodsId}")
    public Response<Map<String, Object>> getSeckillPath(
            @PathVariable String goodsId,
            @RequestParam String verifyCode) {
        // TODO: 实际项目中需要从请求中获取真实的userId
        String userId = "test_user_" + System.currentTimeMillis();
        
        String path = seckillService.generateSeckillPath(goodsId, verifyCode);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("path", path);
        return Response.success(data);
    }
    
    /**
     * 执行秒杀
     */
    @PostMapping("/{path}/execute")
    public Response<SeckillResult> executeSeckill(
            @PathVariable String path,
            @RequestParam String goodsId) {
        // TODO: 实际项目中需要从请求中获取真实的userId
        String userId = "test_user_" + System.currentTimeMillis();
        
        SeckillResult result = seckillService.executeSeckill(path, goodsId, userId);
        return Response.success(result);
    }
    
    /**
     * 获取秒杀结果
     */
    @GetMapping("/result/{goodsId}")
    public Response<SeckillResult> getSeckillResult(@PathVariable String goodsId) {
        // TODO: 实际项目中需要从请求中获取真实的userId
        String userId = "test_user_" + System.currentTimeMillis();
        
        SeckillResult result = seckillService.getSeckillResult(goodsId, userId);
        return Response.success(result);
    }
}
