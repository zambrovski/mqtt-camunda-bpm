package de.techjava.mqtt.camunda.comm;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces a configured MQTT Client.
 * 
 * @author Simon Zambrovski
 */
public class MqttClientFactory {

    private static Logger logger = LoggerFactory.getLogger(MqttClientFactory.class);
    private final String broker;
    private final String clientId;
    private Boolean disabled;

    public MqttClientFactory(final String broker, final String clientId, final Boolean disabled) {
        this.broker = broker;
        if (clientId == null) {
            this.clientId = UUID.randomUUID().toString();
        } else {
            this.clientId = clientId;
        }
        if (disabled == null) {
            this.disabled = Boolean.FALSE;
        } else {
            this.disabled = disabled;
        }
    }

    /**
     * Produces a MQTT client.
     * 
     * @return working instance connected to a broker.
     */
    public MqttClient createMqttClient() {
        MqttClient client = null;
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            if (disabled) {
                logger.warn("MQTT Client is disabled via property. Library is not operational.");
            } else {
                logger.info("Connecting to broker: {}...", broker);
                client.connect(connOpts);
                logger.info("Connected.");
            }
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
    public void destroy(final MqttClient client) {
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
