package com.example.myspringboot.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * OBD数据服务
 * 负责将设备数据存储到SQLite数据库
 */
@Service
public class ObdDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObdDataService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 保存预警上报数据
     */
    public void saveAlarmReport(String deviceId, byte[] message) {
        try {
            if (message.length < 15 + 7) { // 15是固定头长度，7是预警上报数据最小长度
                return;
            }

            int offset = 12;
            int alarmType = message[offset] & 0xFF;
            long alarmTime = bytesToLong(message, offset + 1, 4) * 1000;
            String alarmDetail = new String(message, offset + 5, message.length - 15 - 4);

            String sql = "INSERT INTO alarm_report (device_id, alarm_type, alarm_time, alarm_detail, create_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, deviceId, alarmType, new Date(alarmTime), alarmDetail, new Date());

        } catch (Exception e) {
            LOGGER.error("保存预警上报数据失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 保存指令执行结果
     */
    public void saveCommandResult(String deviceId, byte[] message) {
        try {
            if (message.length < 15 + 3) { // 15是固定头长度，3是指令执行结果最小长度
                return;
            }

            int offset = 12;
            int commandId = bytesToInt(message, offset, 2);
            int result = message[offset + 2] & 0xFF;
            String resultDetail = message.length > 18 ? new String(message, offset + 3, message.length - 15 - 2) : "";

            String sql = "INSERT INTO command_result (device_id, command_id, result, result_detail, create_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, deviceId, commandId, result, resultDetail, new Date());

        } catch (Exception e) {
            LOGGER.error("保存指令执行结果失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 保存车辆启动预警
     */
    public void saveVehicleStartAlarm(String deviceId, byte[] message) {
        try {
            if (message.length < 15 + 12) { // 15是固定头长度，12是车辆启动预警数据长度
                return;
            }

            int offset = 12;
            long startTime = bytesToLong(message, offset, 4) * 1000;
            double longitude = bytesToDouble(message, offset + 4, 4);
            double latitude = bytesToDouble(message, offset + 8, 4);

            String sql = "INSERT INTO vehicle_start_alarm (device_id, start_time, longitude, latitude, create_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, deviceId, new Date(startTime), longitude, latitude, new Date());

        } catch (Exception e) {
            LOGGER.error("保存车辆启动预警失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 保存车辆熄火预警
     */
    public void saveVehicleStopAlarm(String deviceId, byte[] message) {
        try {
            if (message.length < 15 + 12) { // 15是固定头长度，12是车辆熄火预警数据长度
                return;
            }

            int offset = 12;
            long stopTime = bytesToLong(message, offset, 4) * 1000;
            double longitude = bytesToDouble(message, offset + 4, 4);
            double latitude = bytesToDouble(message, offset + 8, 4);

            String sql = "INSERT INTO vehicle_stop_alarm (device_id, stop_time, longitude, latitude, create_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, deviceId, new Date(stopTime), longitude, latitude, new Date());

        } catch (Exception e) {
            LOGGER.error("保存车辆熄火预警失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 保存GPS定位上报
     */
    public void saveGpsReport(String deviceId, byte[] message) {
        try {
            if (message.length < 15 + 18) { // 15是固定头长度，18是GPS定位上报数据长度
                return;
            }

            int offset = 12;
            long gpsTime = bytesToLong(message, offset, 4) * 1000;
            double longitude = bytesToDouble(message, offset + 4, 4);
            double latitude = bytesToDouble(message, offset + 8, 4);
            int speed = bytesToInt(message, offset + 12, 2);
            int direction = bytesToInt(message, offset + 14, 2);

            String sql = "INSERT INTO gps_report (device_id, gps_time, longitude, latitude, speed, direction, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, deviceId, new Date(gpsTime), longitude, latitude, speed, direction, new Date());

        } catch (Exception e) {
            LOGGER.error("保存GPS定位上报失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 字节数组转long
     */
    private long bytesToLong(byte[] bytes, int offset, int length) {
        if (bytes.length < offset + length) {
            return 0;
        }
        long value = 0;
        for (int i = 0; i < length; i++) {
            value <<= 8;
            value |= (bytes[offset + i] & 0xFF);
        }
        return value;
    }

    /**
     * 字节数组转int
     */
    private int bytesToInt(byte[] bytes, int offset, int length) {
        if (bytes.length < offset + length) {
            return 0;
        }
        int value = 0;
        for (int i = 0; i < length; i++) {
            value <<= 8;
            value |= (bytes[offset + i] & 0xFF);
        }
        return value;
    }

    /**
     * 字节数组转double(度分秒格式转度)
     */
    private double bytesToDouble(byte[] bytes, int offset, int length) {
        long value = bytesToLong(bytes, offset, length);
        // 度分秒格式：DDDMM.mmmm
        int degrees = (int) (value / 1000000);
        double minutes = (value % 1000000) / 10000.0;
        return degrees + minutes / 60.0;
    }
}
