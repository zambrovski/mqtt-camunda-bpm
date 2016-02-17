package de.techjava.mqtt.camunda.spring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.techjava.mqtt.camunda.comm.MqttClientFactory;

/**
 * MQTT component configuration.
 * 
 * @author Simon Zambrovski
 */
@Configuration
public class MqttConfiguration {

    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.disabled}")
    private Boolean disabled;

    private MqttClientFactory mqttClientFactory;
    private final List<MqttClient> clients = new ArrayList<MqttClient>();

    @PostConstruct
    public void init() {
        mqttClientFactory = new MqttClientFactory(broker, clientId, disabled);
    }

    @Bean
    public MqttClient createClient() {
        final MqttClient mqttClient = mqttClientFactory.createMqttClient();
        clients.add(mqttClient);
        return mqttClient;
    }

    @PreDestroy
    public void destroyClient() {
        for (final MqttClient client : clients) {
            mqttClientFactory.destroy(client);
        }
    }
}
