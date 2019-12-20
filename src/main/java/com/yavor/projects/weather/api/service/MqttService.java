package com.yavor.projects.weather.api.service;


import com.yavor.projects.weather.api.dto.DeviceStatus;

public interface MqttService {

    void publishLampControl(String deviceId, DeviceStatus status);
}
