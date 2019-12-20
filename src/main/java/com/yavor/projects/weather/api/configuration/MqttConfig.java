package com.yavor.projects.weather.api.configuration;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public IMqttClient mqttClient(@Value("${mqtt.clientId}") String clientId,
                                  @Value("${mqtt.hostname}") String hostname,
                                  @Value("${mqtt.port}") int port){

        try {
            var mqttClient = new MqttClient("tcp://" + hostname + ":" + port, clientId);
            mqttClient.connect(mqttConnectOptions());
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
