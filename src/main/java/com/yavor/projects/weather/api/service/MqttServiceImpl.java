package com.yavor.projects.weather.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavor.projects.weather.api.dto.DeviceStatus;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class MqttServiceImpl implements MqttService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);
    private static final String PUBLISH_LAMP_CONTROL_TOPIC = "devices/{}/in";
    private static final String RECEIVE_LAMP_STATUS_TOPIC = "devices/{}/out";

    private IMqttClient mqttClient;
    private ObjectMapper mapper;

    private LinkedBlockingQueue<DeviceStatus> receivedStatuses;

    public MqttServiceImpl(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
        this.receivedStatuses = new LinkedBlockingQueue<>();
        this.mapper = new ObjectMapper();
    }

    @Override
    public DeviceStatus publishLampControl(String deviceId, DeviceStatus status) {
        try {
            var statusStr = mapper.writeValueAsString(status);
            LOGGER.info("Sending status: {} to device: {}", statusStr, deviceId);
            return publish(PUBLISH_LAMP_CONTROL_TOPIC.replace("{}", deviceId), statusStr);
        } catch (JsonProcessingException e) {
            LOGGER.error("Sending status: {} to device: {} failed. Reason: {}",status, deviceId, e.getMessage());
        }
        return null;
    }

    private DeviceStatus publish(final String topic, final String payload) {
        try {
            var mqttMessage = new MqttMessage();
            mqttMessage.setPayload(payload.getBytes());
            mqttClient.publish(topic, mqttMessage);
            return receivedStatuses.poll(10, TimeUnit.SECONDS);
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void subscribe(final String deviceId) {
        var deviceTopic = RECEIVE_LAMP_STATUS_TOPIC.replace("{}", deviceId);
        LOGGER.info("Subscribe to topic: {}", deviceTopic);

        try {
            mqttClient.subscribeWithResponse(deviceTopic, (topic, msg) -> {
                LOGGER.info("Receive message on topic: {}", topic);
                LOGGER.info("Message: {}", new String(msg.getPayload()));
                var status = mapper.readValue(msg.getPayload(), DeviceStatus.class);
                status.setDeviceId(deviceId);
                receivedStatuses.add(status);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
