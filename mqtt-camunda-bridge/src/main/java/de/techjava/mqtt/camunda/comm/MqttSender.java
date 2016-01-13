package de.techjava.mqtt.camunda.comm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.config.Property;

@ApplicationScoped
@Named
public class MqttSender {

    private static final Logger logger = LoggerFactory.getLogger(MqttSender.class);

    @Inject
    private MqttClient client;

    @Inject
    @Property("mqtt.qos")
    private Integer qos;

    @Inject
    @Property("mqtt.topic.prefix")
    private String topicPrefix;

    public void sendMessage(final String topic, final String content) {
        logger.trace("Publishing message to {}: {}", topic, content);

        final MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        try {
            client.publish(topicPrefix + topic, message);
        } catch (MqttException e) {
            logger.error("Error sending message", e);
        }
        logger.trace("Message published");
    }
}
