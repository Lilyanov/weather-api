package com.yavor.projects.weather.api.service;


import com.yavor.projects.weather.api.dto.DeviceStatus;

public interface MqttService {

    DeviceStatus publishLampControl(String deviceId, DeviceStatus status);

    void subscribe(final String deviceId);
}
