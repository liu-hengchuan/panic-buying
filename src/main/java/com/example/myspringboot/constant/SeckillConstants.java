package com.example.myspringboot.constant;

/**
 * 秒杀系统常量类
 */
public class SeckillConstants {
    
    /**
     * 秒杀活动状态：未开始
     */
    public static final int SECKILL_STATUS_NOT_STARTED = 0;
    
    /**
     * 秒杀活动状态：进行中
     */
    public static final int SECKILL_STATUS_ONGOING = 1;
    
    /**
     * 秒杀活动状态：已结束
     */
    public static final int SECKILL_STATUS_ENDED = 2;
    
    /**
     * 订单状态：创建
     */
    public static final int ORDER_STATUS_CREATED = 0;
    
    /**
     * 订单状态：已支付
     */
    public static final int ORDER_STATUS_PAID = 1;
    
    /**
     * 订单状态：已取消
     */
    public static final int ORDER_STATUS_CANCELED = 2;
    
    /**
     * 订单状态：已完成
     */
    public static final int ORDER_STATUS_COMPLETED = 3;
    
    /**
     * 秒杀结果：失败
     */
    public static final int SECKILL_RESULT_FAILED = 0;
    
    /**
     * 秒杀结果：成功
     */
    public static final int SECKILL_RESULT_SUCCESS = 1;
    
    /**
     * 秒杀结果：处理中
     */
    public static final int SECKILL_RESULT_PROCESSING = 2;
    
    /**
     * 时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 缓存前缀：商品信息
     */
    public static final String CACHE_PREFIX_GOODS = "seckill:goods:";
    
    /**
     * 缓存前缀：库存信息
     */
    public static final String CACHE_PREFIX_STOCK = "seckill:stock:";
    
    /**
     * 缓存前缀：已售用户
     */
    public static final String CACHE_PREFIX_SOLD_USERS = "seckill:sold:users:";
    
    /**
     * 缓存前缀：秒杀路径
     */
    public static final String CACHE_PREFIX_SECKILL_PATH = "seckill:path:";
    
    /**
     * 队列名称：秒杀订单
     */
    public static final String QUEUE_NAME_SECKILL_ORDER = "seckill_order_queue";
    
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 订单超时时间（分钟）
     */
    public static final int ORDER_TIMEOUT_MINUTES = 30;
}
