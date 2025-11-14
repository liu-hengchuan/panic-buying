package com.example.myspringboot;

import com.example.myspringboot.entity.Goods;
import com.example.myspringboot.entity.Order;
import com.example.myspringboot.entity.PageResult;
import com.example.myspringboot.entity.SeckillResult;
import com.example.myspringboot.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeckillApplicationTests {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private GoodsService goodsService;
    
    @Autowired
    private SeckillService seckillService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private StorageService storageService;
    
    private String testGoodsId = "1001";
    private String testUserId = "test_user_123";
    
    @BeforeEach
    void setUp() {
        // 初始化商品数据
        adminService.initGoodsData();
    }
    
    @Test
    void contextLoads() {
        // 测试Spring上下文是否正常加载
        assertNotNull(adminService);
        assertNotNull(goodsService);
        assertNotNull(seckillService);
        assertNotNull(orderService);
        assertNotNull(storageService);
    }
    
    @Test
    void testGoodsService() {
        // 测试获取商品列表
        PageResult<Goods> result = goodsService.getSeckillGoodsList(1, 10, null);
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
        
        // 测试获取商品详情
        Goods goods = goodsService.getGoodsDetail(testGoodsId);
        assertNotNull(goods);
        assertEquals(testGoodsId, goods.getId());
        
        // 测试获取倒计时
        long countdown = goodsService.getSeckillCountdown(testGoodsId);
        assertTrue(countdown > 0 || countdown == -1);
    }
    
    @Test
    void testSeckillService() {
        // 测试生成秒杀路径
        String path = seckillService.generateSeckillPath(testGoodsId, "123456");
        assertNotNull(path);
        assertFalse(path.isEmpty());
        
        // 测试执行秒杀
        SeckillResult result = seckillService.executeSeckill(path, testGoodsId, testUserId);
        assertNotNull(result);
        
        // 检查秒杀结果
        if (result.getResult() == 1) {
            // 秒杀成功
            assertNotNull(result.getOrderId());
            
            // 测试获取秒杀结果
            SeckillResult result2 = seckillService.getSeckillResult(testGoodsId, testUserId);
            assertNotNull(result2);
            assertEquals(1, result2.getResult());
        } else if (result.getResult() == 0) {
            // 秒杀失败，可能库存不足
            assertEquals("库存不足", result.getReason());
        }
    }
    
    @Test
    void testOrderService() {
        // 先执行一次秒杀
        String path = seckillService.generateSeckillPath(testGoodsId, "123456");
        SeckillResult seckillResult = seckillService.executeSeckill(path, testGoodsId, testUserId);
        
        if (seckillResult.getResult() == 1) {
            String orderId = seckillResult.getOrderId();
            
            // 测试获取订单详情
            Order order = orderService.getOrderDetail(orderId);
            assertNotNull(order);
            assertEquals(orderId, order.getOrderId());
            assertEquals(testGoodsId, order.getGoodsId());
            assertEquals(testUserId, order.getUserId());
            
            // 测试获取用户订单列表
            PageResult<Order> ordersResult = orderService.getUserOrders(testUserId, 1, 10, null);
            assertNotNull(ordersResult);
            assertTrue(ordersResult.getTotal() > 0);
            
            // 测试取消订单
            boolean cancelSuccess = orderService.cancelOrder(orderId);
            if (order.getStatus() == 0) {
                // 未支付订单可以取消
                assertTrue(cancelSuccess);
            }
        }
    }
    
    @Test
    void testAdminService() {
        // 测试查看秒杀统计
        Map<String, Object> statistics = adminService.getSeckillStatistics(testGoodsId);
        assertNotNull(statistics);
        assertEquals(testGoodsId, statistics.get("goodsId"));
        
        // 测试重置秒杀活动
        adminService.resetSeckill(testGoodsId);
        int stock = storageService.getStock(testGoodsId);
        Goods goods = storageService.getGoods(testGoodsId);
        assertEquals(goods.getTotalStock(), stock);
    }
    
    @Test
    void testInMemoryStorage() {
        // 测试库存扣减和恢复
        int initialStock = storageService.getStock(testGoodsId);
        boolean deductSuccess = storageService.deductStock(testGoodsId, 1);
        if (initialStock > 0) {
            assertTrue(deductSuccess);
            assertEquals(initialStock - 1, storageService.getStock(testGoodsId));
            
            // 恢复库存
            storageService.restoreStock(testGoodsId, 1);
            assertEquals(initialStock, storageService.getStock(testGoodsId));
        }
        
        // 测试已购买用户
        boolean addSoldUser = storageService.addSoldUser(testGoodsId, testUserId);
        if (addSoldUser) {
            assertTrue(storageService.hasUserPurchased(testGoodsId, testUserId));
            
            // 移除已购买用户
            storageService.removeSoldUser(testGoodsId, testUserId);
            assertFalse(storageService.hasUserPurchased(testGoodsId, testUserId));
        }
    }
}
