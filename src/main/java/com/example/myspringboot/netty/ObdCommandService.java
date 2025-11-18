package com.example.myspringboot.netty;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * OBD指令下发服务
 * 负责向指定设备下发各种指令
 */
@Service
public class ObdCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObdCommandService.class);

    /**
     * 下发电子围栏设置指令
     */
    public boolean sendFenceCommand(String deviceId, int fenceId, int fenceType, 
                                   double longitude, double latitude, int radiusOrPoints) {
        try {
            // 转换经纬度为度分秒格式
            long lon = (long) (longitude * 1000000);
            long lat = (long) (latitude * 1000000);

            // 构建电子围栏设置指令
            byte[] command = buildFenceCommand(deviceId, fenceId, fenceType, lon, lat, radiusOrPoints);

            // 发送指令
            return sendCommand(deviceId, command);

        } catch (Exception e) {
            LOGGER.error("下发电子围栏指令失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 下发通用指令
     */
    public boolean sendGeneralCommand(String deviceId, int commandId, int commandType, byte[] params) {
        try {
            // 构建通用指令
            byte[] command = buildGeneralCommand(deviceId, commandId, commandType, params);

            // 发送指令
            return sendCommand(deviceId, command);

        } catch (Exception e) {
            LOGGER.error("下发通用指令失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 构建电子围栏设置指令
     */
    private byte[] buildFenceCommand(String deviceId, int fenceId, int fenceType, 
                                     long longitude, long latitude, int radiusOrPoints) {
        int dataLength = 2 + 1 + 4 + 4 + 4;
        int totalLength = 15 + dataLength;
        byte[] command = new byte[totalLength];

        command[0] = (byte) 0xAA; // 起始符

        // 设备ID
        byte[] deviceIdBytes = hexStringToBytes(deviceId);
        System.arraycopy(deviceIdBytes, 0, command, 1, 8);

        command[9] = (byte) 0x04; // 功能码：电子围栏设置

        // 数据长度
        command[10] = (byte) ((dataLength >> 8) & 0xFF);
        command[11] = (byte) (dataLength & 0xFF);

        int offset = 12;

        // 围栏ID
        command[offset++] = (byte) ((fenceId >> 8) & 0xFF);
        command[offset++] = (byte) (fenceId & 0xFF);

        // 围栏类型
        command[offset++] = (byte) (fenceType & 0xFF);

        // 经度
        command[offset++] = (byte) ((longitude >> 24) & 0xFF);
        command[offset++] = (byte) ((longitude >> 16) & 0xFF);
        command[offset++] = (byte) ((longitude >> 8) & 0xFF);
        command[offset++] = (byte) (longitude & 0xFF);

        // 纬度
        command[offset++] = (byte) ((latitude >> 24) & 0xFF);
        command[offset++] = (byte) ((latitude >> 16) & 0xFF);
        command[offset++] = (byte) ((latitude >> 8) & 0xFF);
        command[offset++] = (byte) (latitude & 0xFF);

        // 半径/顶点数
        command[offset++] = (byte) ((radiusOrPoints >> 24) & 0xFF);
        command[offset++] = (byte) ((radiusOrPoints >> 16) & 0xFF);
        command[offset++] = (byte) ((radiusOrPoints >> 8) & 0xFF);
        command[offset++] = (byte) (radiusOrPoints & 0xFF);

        // 校验码
        command[totalLength - 2] = calculateChecksum(command);
        // 结束符
        command[totalLength - 1] = (byte) 0x55;

        return command;
    }

    /**
     * 构建通用指令
     */
    private byte[] buildGeneralCommand(String deviceId, int commandId, int commandType, byte[] params) {
        int paramsLength = params != null ? params.length : 0;
        int dataLength = 2 + 1 + paramsLength;
        int totalLength = 15 + dataLength;
        byte[] command = new byte[totalLength];

        command[0] = (byte) 0xAA; // 起始符

        // 设备ID
        byte[] deviceIdBytes = hexStringToBytes(deviceId);
        System.arraycopy(deviceIdBytes, 0, command, 1, 8);

        command[9] = (byte) 0x06; // 功能码：指令下发

        // 数据长度
        command[10] = (byte) ((dataLength >> 8) & 0xFF);
        command[11] = (byte) (dataLength & 0xFF);

        int offset = 12;

        // 指令ID
        command[offset++] = (byte) ((commandId >> 8) & 0xFF);
        command[offset++] = (byte) (commandId & 0xFF);

        // 指令类型
        command[offset++] = (byte) (commandType & 0xFF);

        // 指令参数
        if (params != null && params.length > 0) {
            System.arraycopy(params, 0, command, offset, params.length);
        }

        // 校验码
        command[totalLength - 2] = calculateChecksum(command);
        // 结束符
        command[totalLength - 1] = (byte) 0x55;

        return command;
    }

    /**
     * 发送指令
     */
    private boolean sendCommand(String deviceId, byte[] command) {
        Channel channel = ObdHandler.DEVICE_CHANNEL_MAP.get(deviceId);
        if (channel == null || !channel.isActive()) {
            LOGGER.error("设备[{}]未连接或连接已关闭", deviceId);
            return false;
        }

        try {
            channel.writeAndFlush(command).sync();
            LOGGER.info("指令已下发到设备[{}]：{}", deviceId, bytesToHexString(command));
            return true;
        } catch (Exception e) {
            LOGGER.error("发送指令失败：{}", e.getMessage(), e);
            return false;
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
     * 十六进制字符串转字节数组
     */
    private byte[] hexStringToBytes(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int length = hexString.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
        return bytes;
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex.toUpperCase());
            sb.append(' ');
        }
        return sb.toString().trim();
    }
}
