package com.example.myspringboot.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OBD设备消息处理器
 * 处理设备的各种消息类型和心跳检测
 */
@Component
public class ObdHandler extends SimpleChannelInboundHandler<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObdHandler.class);

    // 存储设备ID与Channel的映射
    public static final Map<String, Channel> DEVICE_CHANNEL_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ObdMessageProcessor messageProcessor;

    /**
     * 设备连接建立
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("设备连接建立：{}", channel.remoteAddress());
    }

    /**
     * 设备连接关闭
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("设备连接关闭：{}", channel.remoteAddress());
        // 移除设备映射
        DEVICE_CHANNEL_MAP.entrySet().removeIf(entry -> entry.getValue() == channel);
    }

    /**
     * 心跳检测
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 读空闲，断开连接
                LOGGER.info("设备心跳超时，断开连接：{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理设备消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        try {
            // 解析设备ID
            String deviceId = bytesToHexString(msg, 1, 8);
            LOGGER.info("收到设备[{}]消息：{}", deviceId, bytesToHexString(msg));

            // 存储设备与Channel的映射
            DEVICE_CHANNEL_MAP.put(deviceId, ctx.channel());

            // 处理消息
            messageProcessor.processMessage(deviceId, msg);

        } catch (Exception e) {
            LOGGER.error("处理设备消息失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("设备连接异常：{}", cause.getMessage(), cause);
        ctx.channel().close();
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHexString(byte[] bytes, int start, int length) {
        byte[] subArray = new byte[length];
        System.arraycopy(bytes, start, subArray, 0, length);
        return bytesToHexString(subArray);
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
