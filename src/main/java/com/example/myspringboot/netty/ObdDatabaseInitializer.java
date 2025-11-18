package com.example.myspringboot.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * OBD数据库初始化
 * 负责创建OBD相关的数据表
 */
@Component
public class ObdDatabaseInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObdDatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        createAlarmReportTable();
        createCommandResultTable();
        createVehicleStartAlarmTable();
        createVehicleStopAlarmTable();
        createGpsReportTable();
    }

    /**
     * 创建预警上报表
     */
    private void createAlarmReportTable() {
        String sql = "CREATE TABLE IF NOT EXISTS alarm_report (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_id VARCHAR(16) NOT NULL, " +
                "alarm_type INTEGER NOT NULL, " +
                "alarm_time TIMESTAMP NOT NULL, " +
                "alarm_detail TEXT, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
        LOGGER.info("创建alarm_report表成功");
    }

    /**
     * 创建指令执行结果表
     */
    private void createCommandResultTable() {
        String sql = "CREATE TABLE IF NOT EXISTS command_result (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_id VARCHAR(16) NOT NULL, " +
                "command_id INTEGER NOT NULL, " +
                "result INTEGER NOT NULL, " +
                "result_detail TEXT, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
        LOGGER.info("创建command_result表成功");
    }

    /**
     * 创建车辆启动预警表
     */
    private void createVehicleStartAlarmTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vehicle_start_alarm (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_id VARCHAR(16) NOT NULL, " +
                "start_time TIMESTAMP NOT NULL, " +
                "longitude DOUBLE NOT NULL, " +
                "latitude DOUBLE NOT NULL, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
        LOGGER.info("创建vehicle_start_alarm表成功");
    }

    /**
     * 创建车辆熄火预警表
     */
    private void createVehicleStopAlarmTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vehicle_stop_alarm (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_id VARCHAR(16) NOT NULL, " +
                "stop_time TIMESTAMP NOT NULL, " +
                "longitude DOUBLE NOT NULL, " +
                "latitude DOUBLE NOT NULL, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
        LOGGER.info("创建vehicle_stop_alarm表成功");
    }

    /**
     * 创建GPS定位上报表
     */
    private void createGpsReportTable() {
        String sql = "CREATE TABLE IF NOT EXISTS gps_report (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_id VARCHAR(16) NOT NULL, " +
                "gps_time TIMESTAMP NOT NULL, " +
                "longitude DOUBLE NOT NULL, " +
                "latitude DOUBLE NOT NULL, " +
                "speed INTEGER NOT NULL, " +
                "direction INTEGER NOT NULL, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        jdbcTemplate.execute(sql);
        LOGGER.info("创建gps_report表成功");
    }
}