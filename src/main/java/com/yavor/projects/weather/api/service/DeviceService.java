package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.dto.DeviceStatus;
import com.yavor.projects.weather.api.dto.ScheduleDto;

import java.util.List;

public interface DeviceService {

    List<DeviceDto> findAllDevices();

    DeviceDto findDeviceById(String deviceId);

    void switchDevice(String deviceId, DeviceStatus status);

    ScheduleDto scheduleDeviceSwitch(String deviceId, DeviceStatus status);
}
