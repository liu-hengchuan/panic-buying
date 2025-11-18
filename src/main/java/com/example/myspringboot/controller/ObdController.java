package com.example.myspringboot.controller;

import com.example.myspringboot.netty.ObdCommandService;
import com.example.myspringboot.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * OBD设备管理控制器
 * 提供指令下发等API
 */
@RestController
@RequestMapping("/api/obd")
public class ObdController {

    @Autowired
    private ObdCommandService obdCommandService;

    /**
     * 下发电子围栏设置指令
     */
    @PostMapping("/fence")
    public Response<String> sendFenceCommand(@RequestParam String deviceId,
                                              @RequestParam int fenceId,
                                              @RequestParam int fenceType,
                                              @RequestParam double longitude,
                                              @RequestParam double latitude,
                                              @RequestParam int radiusOrPoints) {
        try {
            boolean success = obdCommandService.sendFenceCommand(
                    deviceId, fenceId, fenceType, longitude, latitude, radiusOrPoints);
            if (success) {
                return Response.success("指令下发成功");
            } else {
                return Response.error("指令下发失败");
            }
        } catch (Exception e) {
            return Response.error("指令下发失败：" + e.getMessage());
        }
    }

    /**
     * 下发通用指令
     */
    @PostMapping("/command")
    public Response<String> sendGeneralCommand(@RequestParam String deviceId,
                                                @RequestParam int commandId,
                                                @RequestParam int commandType,
                                                @RequestParam(required = false) String params) {
        try {
            byte[] paramBytes = params != null ? params.getBytes() : null;
            boolean success = obdCommandService.sendGeneralCommand(
                    deviceId, commandId, commandType, paramBytes);
            if (success) {
                return Response.success("指令下发成功");
            } else {
                return Response.error("指令下发失败");
            }
        } catch (Exception e) {
            return Response.error("指令下发失败：" + e.getMessage());
        }
    }
}
