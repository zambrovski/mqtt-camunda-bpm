package de.techjava.mqtt.camunda.comm;

import java.util.UUID;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.config.Property;

/**
 * Produces a configured MQTT Client.
 * 
 * @author Simon Zambrovski
 */
public class MqttClientProducer {

    private static Logger logger = LoggerFactory.getLogger(MqttClientProducer.class);

    @Inject
    @Property("mqtt.broker")
    private String broker;

    @Inject
    @Property("mqtt.client.id")
    private String clientId;

    /**
     * Produces a MQTT client.
     * 
     * @return working instance connected to a broker.
     */
    @Produces
    public MqttClient createMqttClient() {
        MqttClient client = null;
        try {
            if (clientId == null) {
                clientId = UUID.randomUUID().toString();
            }
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.info("Connecting to broker: {}...", broker);
            client.connect(connOpts);
            logger.info("Connected.");
        } catch (MqttException e) {
            logger.error("Error establishing MQTT connection", e);
        }
        return client;
    }

    /**
     * Shuts down the MQTT client.
     * 
     * @param client
     *            client to shut down.
     */
    public void destroy(@Disposes MqttClient client) {
        try {
            if (client != null) {
                client.disconnect();
                logger.info("Disconnected from broker: " + broker);
            }
        } catch (MqttException e) {
            logger.error("Error disconnecting", e);
        }
    }

}
