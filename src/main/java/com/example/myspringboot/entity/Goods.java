package com.example.myspringboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Goods {
    private String id;
    private String name;
    private String title;
    private String image;
    private double price;
    private double seckillPrice;
    private int totalStock;
    private int availableStock;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private int status; // 0-未开始, 1-进行中, 2-已结束
    private long countdown; // 距离开始/结束的剩余时间(毫秒)
}