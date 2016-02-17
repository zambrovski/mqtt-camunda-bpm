package de.techjava.mqtt.camunda.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.paho.client.mqttv3.MqttClient;

import de.techjava.mqtt.camunda.comm.MqttHelper;
import de.techjava.mqtt.camunda.config.Property;

@Named
@ApplicationScoped
public class MqttSender {

    @Inject
    private MqttClient client;

    @Inject
    @Property("mqtt.qos")
    private Integer qos;

    @Inject
    @Property("mqtt.topic.prefix")
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
