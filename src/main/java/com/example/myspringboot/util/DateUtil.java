package com.example.myspringboot.util;

import com.example.myspringboot.constant.SeckillConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 */
public class DateUtil {
    
    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(SeckillConstants.DATE_TIME_FORMAT);
    
    /**
     * 将LocalDateTime格式化为字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }
    
    /**
     * 将字符串解析为LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }
    
    /**
     * 获取当前时间
     */
    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
    
    /**
     * 检查时间是否在指定区间内
     */
    public static boolean isBetween(LocalDateTime time, LocalDateTime startTime, LocalDateTime endTime) {
        if (time == null || startTime == null || endTime == null) {
            return false;
        }
        return time.isAfter(startTime) && time.isBefore(endTime);
    }
}
