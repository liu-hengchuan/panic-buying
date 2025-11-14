package com.example.myspringboot.entity;

import lombok.Data;

@Data
public class SeckillResult {
    private int result; // 0-失败, 1-成功, 2-处理中
    private String orderId;
    private String reason;
}