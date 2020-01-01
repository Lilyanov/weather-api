package com.yavor.projects.weather.api.service;


import com.yavor.projects.weather.api.dto.DeviceStatus;

public interface MqttService {

    DeviceStatus publishLampControl(DeviceStatus status);
}
