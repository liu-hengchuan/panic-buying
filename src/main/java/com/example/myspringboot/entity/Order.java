package com.example.myspringboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private String orderId;
    private String userId;
    private String goodsId;
    private int quantity;
    private double price;
    private int status; // 0-创建, 1-已支付, 2-已完成, 3-已取消
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime payTime;
}