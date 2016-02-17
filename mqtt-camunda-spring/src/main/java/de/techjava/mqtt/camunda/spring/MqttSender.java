package de.techjava.mqtt.camunda.spring;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.techjava.mqtt.camunda.comm.MqttHelper;

@Component
public class MqttSender {

    @Autowired
    private MqttClient client;

    @Value("${mqtt.qos}")
    private Integer qos;

    @Value("${mqtt.topic.prefix}")
    private String topicPrefix;

    private MqttHelper mqttHelper;

    @PostConstruct
    public void init() {
        mqttHelper = new MqttHelper(client, topicPrefix, qos);
    }

    public void sendMessage(final String topic, final String content) {
        mqttHelper.sendMessage(topic, content);
    }

    public MqttHelper getMqttHelper() {
        return mqttHelper;
    }
}
