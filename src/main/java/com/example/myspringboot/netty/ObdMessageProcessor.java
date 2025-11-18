package com.example.myspringboot.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OBD消息处理器
 * 根据不同功能码处理具体业务逻辑
 */
@Component
public class ObdMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObdMessageProcessor.class);

    @Autowired
    private ObdDataService obdDataService;

    /**
     * 处理设备消息
     */
    public void processMessage(String deviceId, byte[] message) {
        if (message.length < 15) {
            LOGGER.error("无效的消息长度：{}", message.length);
            return;
        }

        // 验证起始符和结束符
        if (message[0] != (byte) 0xAA || message[message.length - 1] != (byte) 0x55) {
            LOGGER.error("无效的起始符或结束符");
            return;
        }

        // 验证校验码
        byte checksum = calculateChecksum(message);
        if (checksum != message[message.length - 2]) {
            LOGGER.error("校验码错误：预期[{}]，实际[{}]", checksum, message[message.length - 2]);
            return;
        }

        // 获取功能码
        byte functionCode = message[9];

        // 根据功能码处理消息
        switch (functionCode) {
            case (byte) 0x01: // 心跳请求
                processHeartbeatRequest(deviceId, message);
                break;
            case (byte) 0x03: // 预警上报
                processAlarmReport(deviceId, message);
                break;
            case (byte) 0x07: // 指令执行结果
                processCommandResult(deviceId, message);
                break;
            case (byte) 0x08: // 对时请求
                processTimeSyncRequest(deviceId, message);
                break;
            case (byte) 0x0A: // 车辆启动预警
                processVehicleStartAlarm(deviceId, message);
                break;
            case (byte) 0x0B: // 车辆熄火预警
                processVehicleStopAlarm(deviceId, message);
                break;
            case (byte) 0x0C: // GPS定位上报
                processGpsReport(deviceId, message);
                break;
            default:
                LOGGER.error("未知的功能码：{}", functionCode);
        }
    }

    /**
     * 计算校验码
     */
    private byte calculateChecksum(byte[] message) {
        byte checksum = 0;
        // 从起始符之后到校验码之前
        for (int i = 1; i < message.length - 2; i++) {
            checksum ^= message[i];
        }
        return checksum;
    }

    /**
     * 处理心跳请求
     */
    private void processHeartbeatRequest(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的心跳请求", deviceId);
        // 返回心跳响应
        byte[] response = buildHeartbeatResponse(message);
        ObdHandler.DEVICE_CHANNEL_MAP.get(deviceId).writeAndFlush(response);
    }

    /**
     * 构建心跳响应
     */
    private byte[] buildHeartbeatResponse(byte[] request) {
        byte[] response = new byte[15];
        System.arraycopy(request, 0, response, 0, 9); // 复制起始符、设备ID
        response[9] = (byte) 0x02; // 功能码：心跳响应
        response[10] = 0x00; // 数据长度高字节
        response[11] = 0x00; // 数据长度低字节
        response[12] = calculateChecksum(response); // 校验码
        response[13] = (byte) 0x55; // 结束符
        return response;
    }

    /**
     * 处理预警上报
     */
    private void processAlarmReport(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的预警上报", deviceId);
        // 保存到数据库
        obdDataService.saveAlarmReport(deviceId, message);
    }

    /**
     * 处理指令执行结果
     */
    private void processCommandResult(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的指令执行结果", deviceId);
        // 保存到数据库
        obdDataService.saveCommandResult(deviceId, message);
    }

    /**
     * 处理对时请求
     */
    private void processTimeSyncRequest(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的对时请求", deviceId);
        // 返回对时响应
        byte[] response = buildTimeSyncResponse(message);
        ObdHandler.DEVICE_CHANNEL_MAP.get(deviceId).writeAndFlush(response);
    }

    /**
     * 构建对时响应
     */
    private byte[] buildTimeSyncResponse(byte[] request) {
        byte[] response = new byte[19];
        System.arraycopy(request, 0, response, 0, 9); // 复制起始符、设备ID
        response[9] = (byte) 0x09; // 功能码：对时响应
        response[10] = 0x00; // 数据长度高字节
        response[11] = 0x04; // 数据长度低字节

        // 设置服务器时间
        long currentTime = System.currentTimeMillis() / 1000;
        response[12] = (byte) ((currentTime >> 24) & 0xFF);
        response[13] = (byte) ((currentTime >> 16) & 0xFF);
        response[14] = (byte) ((currentTime >> 8) & 0xFF);
        response[15] = (byte) (currentTime & 0xFF);

        response[16] = calculateChecksum(response); // 校验码
        response[17] = (byte) 0x55; // 结束符
        return response;
    }

    /**
     * 处理车辆启动预警
     */
    private void processVehicleStartAlarm(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的车辆启动预警", deviceId);
        // 保存到数据库
        obdDataService.saveVehicleStartAlarm(deviceId, message);
    }

    /**
     * 处理车辆熄火预警
     */
    private void processVehicleStopAlarm(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的车辆熄火预警", deviceId);
        // 保存到数据库
        obdDataService.saveVehicleStopAlarm(deviceId, message);
    }

    /**
     * 处理GPS定位上报
     */
    private void processGpsReport(String deviceId, byte[] message) {
        LOGGER.info("处理设备[{}]的GPS定位上报", deviceId);
        // 保存到数据库
        obdDataService.saveGpsReport(deviceId, message);
    }
}
