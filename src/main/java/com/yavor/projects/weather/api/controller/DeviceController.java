package com.yavor.projects.weather.api.controller;

import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.dto.DeviceStatus;
import com.yavor.projects.weather.api.dto.ScheduleDto;
import com.yavor.projects.weather.api.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDto>> getAll() {
        var devices = deviceService.findAllDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("{deviceId}")
    public ResponseEntity<DeviceDto> getDevice(@PathVariable("deviceId") String deviceId) {
        var device = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping("{deviceId}/switch")
    public ResponseEntity<DeviceStatus> switchControl(@PathVariable("deviceId") String deviceId, @RequestBody DeviceStatus status) {
        deviceService.switchDevice(deviceId, status);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("{deviceId}/schedule")
    public ResponseEntity<List<ScheduleDto>> scheduleSwitchControl(@PathVariable("deviceId") String deviceId, @RequestBody List<ScheduleDto> schedules) {
        var persistedSchedules = deviceService.scheduleDeviceSwitch(deviceId, schedules);
        return new ResponseEntity<>(persistedSchedules, HttpStatus.OK);
    }
}
