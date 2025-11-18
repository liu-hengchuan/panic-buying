package com.example.myspringboot.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * OBD设备Netty服务器
 * 负责处理设备的TCP连接、心跳检测和消息收发
 */
@Component
public class ObdServer {

    private static final int PORT = 8888;
    private static final int READ_IDLE_TIME = 60; // 读空闲时间(秒)
    private static final int WRITE_IDLE_TIME = 0;
    private static final int ALL_IDLE_TIME = 0;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;

    @Autowired
    private ObdHandler obdHandler;

    /**
     * 服务器启动
     */
    @PostConstruct
    public void start() throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 粘包拆包处理
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                                    1024, 10, 2, 0, 0));
                            // 心跳检测
                            ch.pipeline().addLast(new IdleStateHandler(
                                    READ_IDLE_TIME, WRITE_IDLE_TIME, ALL_IDLE_TIME,
                                    TimeUnit.SECONDS));
                            // 自定义处理器
                            ch.pipeline().addLast(obdHandler);
                        }
                    });

            channelFuture = bootstrap.bind(PORT).sync();
            System.out.println("OBD Netty服务器启动成功，端口：" + PORT);
        } catch (InterruptedException e) {
            System.err.println("OBD Netty服务器启动失败：" + e.getMessage());
            throw e;
        }
    }

    /**
     * 服务器关闭
     */
    @PreDestroy
    public void stop() throws InterruptedException {
        try {
            if (channelFuture != null) {
                channelFuture.channel().close().sync();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("OBD Netty服务器已关闭");
        }
    }
}
