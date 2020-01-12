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

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MqttServiceImpl implements MqttService {
    public static AtomicBoolean CONNECTION_INTERRUPTED = new AtomicBoolean(false);

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);
    private static final String PUBLISH_LAMP_CONTROL_TOPIC = "devices/in";
    private static final String RECEIVE_LAMP_STATUS_TOPIC = "devices/out";

    private IMqttClient mqttClient;
    private ObjectMapper mapper;

    private LinkedBlockingQueue<DeviceStatus> receivedStatuses;

    public MqttServiceImpl(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
        this.receivedStatuses = new LinkedBlockingQueue<>();
        this.mapper = new ObjectMapper();
    }

    @PostConstruct
    public void initializeSubscribtions() {
        subscribe();
    }

    @Override
    public DeviceStatus publishLampControl(DeviceStatus status) {
        byte retries = 0;
        while (retries < 3) {
            try {
                var statusStr = mapper.writeValueAsString(status);
                LOGGER.info("Sending status: {} to device: {}", statusStr);
                publish(PUBLISH_LAMP_CONTROL_TOPIC, statusStr);
                return receivedStatuses.poll(10, TimeUnit.SECONDS);
            } catch (JsonProcessingException | InterruptedException e) {
                LOGGER.error("Sending status: {} to device: {} failed. Reason: {}", status, e.getMessage());
                retries++;
            }
        }
        return null;
    }

    private void publish(final String topic, final String payload) {
        if (CONNECTION_INTERRUPTED.get()) {
            LOGGER.warn("Reinitializing the subscriptions after connection interrupted!");
            this.subscribe();
            CONNECTION_INTERRUPTED.set(false);
        }
        try {
            var mqttMessage = new MqttMessage();
            mqttMessage.setPayload(payload.getBytes());
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
            LOGGER.error("Couldn't publish message: {}", e.getMessage());
            LOGGER.info("Is connected: {}", mqttClient.isConnected());
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect();
                    mqttClient.connect();
                }
            } catch (MqttException e1) {
                LOGGER.error("Couldn't reconnect: {}", e1.getMessage());
            }
        }
    }

    private void subscribe() {
        LOGGER.info("Subscribe to topic: {}", RECEIVE_LAMP_STATUS_TOPIC);
        try {
            mqttClient.subscribeWithResponse(RECEIVE_LAMP_STATUS_TOPIC, (topic, msg) -> {
                LOGGER.info("Receive message on topic: {}", topic);
                LOGGER.info("Message: {}", new String(msg.getPayload()));
                var status = mapper.readValue(msg.getPayload(), DeviceStatus.class);
                receivedStatuses.add(status);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
