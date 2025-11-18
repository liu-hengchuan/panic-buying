# OBD物联网后端实现说明

## 1. 项目概述
本项目基于Spring Boot和Netty实现了一个高性能的OBD物联网后端，支持上万设备的并发连接，具备以下功能：
- 心跳检测与管理
- 预警信息上报与存储
- 电子围栏设置
- 指令下发与执行结果反馈
- 对时功能
- 车辆启动/熄火预警
- GPS定位上报

## 2. 技术架构

### 2.1 核心技术栈
- **Spring Boot 2.7.18**：应用框架
- **Netty 4.1.109**：高性能网络通信框架
- **SQLite**：嵌入式数据库
- **MyBatis Plus**：ORM框架

### 2.2 项目结构
```
com.example.myspringboot
├── netty/              # Netty相关组件
│   ├── ObdServer.java            # Netty服务器
│   ├── ObdHandler.java           # 消息处理器
│   ├── ObdMessageProcessor.java  # 业务逻辑处理器
│   ├── ObdDataService.java       # 数据存储服务
│   ├── ObdCommandService.java    # 指令下发服务
│   └── ObdDatabaseInitializer.java # 数据库初始化
├── controller/         # Web控制器
│   └── ObdController.java        # OBD管理API
├── entity/             # 实体类
├── mapper/             # 数据访问层
└── service/            # 业务服务层
```

## 3. 核心功能实现

### 3.1 Netty服务器启动
**位置**：`com.example.myspringboot.netty.ObdServer.java`

**核心逻辑**：
- 使用NIO模型，支持高并发
- 配置`LengthFieldBasedFrameDecoder`处理粘包拆包
- 添加`IdleStateHandler`实现心跳检测
- 启动端口：8888

```java
@PostConstruct
public void start() throws InterruptedException {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 10, 2, 0, 0));
                    ch.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(obdHandler);
                }
            });
    channelFuture = bootstrap.bind(PORT).sync();
}
```

### 3.2 消息处理流程
**位置**：`com.example.myspringboot.netty.ObdHandler.java` 和 `ObdMessageProcessor.java`

**核心逻辑**：
1. 设备连接建立后，将设备ID与Channel映射存储到`DEVICE_CHANNEL_MAP`中
2. 收到设备消息后，验证起始符、结束符和校验码
3. 根据功能码分发到不同的业务处理方法
4. 处理完成后返回响应或保存数据

### 3.3 心跳检测
**位置**：`com.example.myspringboot.netty.ObdHandler.java`

**核心逻辑**：
- 读空闲时间设置为60秒
- 当检测到空闲时，主动断开连接
- 设备定期发送心跳请求(0x01)，服务器返回心跳响应(0x02)

```java
@Override
public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.READER_IDLE) {
            LOGGER.info("设备心跳超时，断开连接：{}", ctx.channel().remoteAddress());
            ctx.channel().close();
        }
    }
    super.userEventTriggered(ctx, evt);
}
```

### 3.4 指令下发
**位置**：`com.example.myspringboot.netty.ObdCommandService.java`

**核心逻辑**：
1. 根据业务需求构建指令报文
2. 从`DEVICE_CHANNEL_MAP`中获取设备对应的Channel
3. 调用`writeAndFlush`方法发送指令

```java
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
```

### 3.5 数据存储
**位置**：`com.example.myspringboot.netty.ObdDataService.java`

**核心逻辑**：
- 将设备上报的数据解析后存储到SQLite数据库
- 支持以下表：
  - `alarm_report`：预警上报
  - `command_result`：指令执行结果
  - `vehicle_start_alarm`：车辆启动预警
  - `vehicle_stop_alarm`：车辆熄火预警
  - `gps_report`：GPS定位上报

## 4. 数据库设计

### 4.1 预警上报表(alarm_report)
```sql
CREATE TABLE alarm_report (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id VARCHAR(16) NOT NULL,
    alarm_type INTEGER NOT NULL,
    alarm_time TIMESTAMP NOT NULL,
    alarm_detail TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

### 4.2 GPS定位上报表(gps_report)
```sql
CREATE TABLE gps_report (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id VARCHAR(16) NOT NULL,
    gps_time TIMESTAMP NOT NULL,
    longitude DOUBLE NOT NULL,
    latitude DOUBLE NOT NULL,
    speed INTEGER NOT NULL,
    direction INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
```

## 5. API接口

### 5.1 电子围栏设置
**URL**：`/api/obd/fence`
**Method**：POST
**Params**：
- deviceId：设备ID
- fenceId：围栏ID
- fenceType：围栏类型(1:圆形, 2:多边形)
- longitude：经度
- latitude：纬度
- radiusOrPoints：半径/顶点数

### 5.2 通用指令下发
**URL**：`/api/obd/command`
**Method**：POST
**Params**：
- deviceId：设备ID
- commandId：指令ID
- commandType：指令类型(1:查询, 2:控制)
- params：指令参数

## 6. 性能优化

### 6.1 并发处理
- Netty的NIO模型支持高并发
- 使用`EventLoopGroup`线程池处理请求
- 设备连接映射使用`ConcurrentHashMap`保证线程安全

### 6.2 内存管理
- 使用ByteBuf避免内存拷贝
- 合理设置空闲时间，及时释放资源

### 6.3 数据库优化
- 使用SQLite的索引提高查询效率
- 批量插入减少IO操作

## 7. 最佳实践

### 7.1 消息格式设计
- 固定起始符和结束符
- 包含长度字段和校验码
- 功能码清晰明确

### 7.2 异常处理
- 所有异常都有日志记录
- 连接异常时及时关闭Channel
- 消息格式错误时不影响其他设备

### 7.3 监控与日志
- 详细记录设备连接、消息收发、指令执行等信息
- 使用Slf4j进行日志管理

## 8. 启动说明

### 8.1 环境要求
- JDK 1.8+
- Maven 3.6+

### 8.2 启动步骤
1. 克隆代码：`git clone -b buying-sqlite-3 https://github.com/liu-hengchuan/panic-buying.git`
2. 进入项目目录：`cd panic-buying`
3. 下载依赖：`mvn install`
4. 启动项目：`java -jar target/myspringboot-0.0.1-SNAPSHOT.jar`

### 8.3 端口说明
- Netty服务器端口：8888
- Web API端口：8080
