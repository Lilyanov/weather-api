package com.yavor.projects.weather.api.configuration;

import com.yavor.projects.weather.api.service.MqttServiceImpl;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

    @Bean
    public IMqttClient mqttClient(@Value("${mqtt.clientId}") String clientId,
                                  @Value("${mqtt.hostname}") String hostname,
                                  @Value("${mqtt.port}") int port) {

        try {
            var mqttClient = new MqttClient("tcp://" + hostname + ":" + port, clientId);
            mqttClient.connect(mqttConnectOptions());
            mqttClient.setCallback(new MqttCallbackExtended() {
                public void connectComplete(boolean reconnect, String hostname) {
                    LOGGER.warn("Connection has been established to host: {},  reconnect: {}", hostname, reconnect);
                }

                public void connectionLost(Throwable cause) {
                    LOGGER.warn("Connection has been lost! Reason: {}", cause.getMessage());
                    MqttServiceImpl.CONNECTION_INTERRUPTED.set(true);
                }

                public void messageArrived(String topic, MqttMessage message) {
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });
            return mqttClient;
        } catch (MqttException e) {
            System.err.println("couldn't create client");
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    @ConfigurationProperties(prefix = "mqtt")
    public MqttConnectOptions mqttConnectOptions() {
        return new MqttConnectOptions();
    }
}
