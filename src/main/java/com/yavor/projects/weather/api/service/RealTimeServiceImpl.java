package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.configuration.WebsocketConfig;
import com.yavor.projects.weather.api.dto.DeviceDto;
import com.yavor.projects.weather.api.entity.Timeseries;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealTimeServiceImpl implements RealTimeService {

    private SimpMessagingTemplate simpleMessagingTemplate;


    public RealTimeServiceImpl(SimpMessagingTemplate simpleMessagingTemplate) {
        this.simpleMessagingTemplate = simpleMessagingTemplate;
    }

    public void sendDevice(DeviceDto device) {
        simpleMessagingTemplate.convertAndSend(WebsocketConfig.DEVICES_TOPIC,
                new ResponseEntity<>(device, HttpStatus.OK));
    }

    public void sendTimeseries(List<Timeseries> timeseries) {
        simpleMessagingTemplate.convertAndSend(WebsocketConfig.MEASUREMENTS_TOPIC,
                new ResponseEntity<>(timeseries, HttpStatus.CREATED));
    }

    public void sendHeartBeat() {
        simpleMessagingTemplate.convertAndSend(WebsocketConfig.MEASUREMENTS_TOPIC,
                new ResponseEntity<>("OK", HttpStatus.NO_CONTENT));
    }

}