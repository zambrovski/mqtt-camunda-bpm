package de.techjava.mqtt.camunda.comm;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttHelper {

    private static final Logger logger = LoggerFactory.getLogger(MqttHelper.class);

    private MqttClient client;
    private Integer qos;
    private String topicPrefix;

    public MqttHelper(final MqttClient client, final String topicPrefix, final Integer qos) {
        this.client = client;
        this.topicPrefix = topicPrefix;
        this.qos = qos;
    }

    public void sendMessage(final String topic, final String content) {
        logger.trace("Publishing message to {}: {}", topic, content);

        final MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        try {
            client.publish(topicPrefix + topic, message);
        } catch (MqttException e) {
            logger.error("Error sending message", e);
        }
        logger.trace("Message published.");
    }
}
