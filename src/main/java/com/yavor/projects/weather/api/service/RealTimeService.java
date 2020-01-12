package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.entity.Timeseries;

import java.util.List;

public interface RealTimeService {

    void sendDevice(DeviceDto device);

    void sendTimeseries(List<Timeseries> timeseries);

    void sendHeartBeat();
}
