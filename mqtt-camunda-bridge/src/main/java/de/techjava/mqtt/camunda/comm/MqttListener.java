package de.techjava.mqtt.camunda.comm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;

/**
 * MqttListener Bean. Manages all subscriptions to MQTT.
 * 
 * @author Thorsten Pohl
 * @author Simon Zambrovski
 */
public class MqttListener extends MqttCallbackAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MqttListener.class);
    private Map<String, Collection<MqttCallback>> listeners = new HashMap<String, Collection<MqttCallback>>();

    private MqttClient client;
    private CatchingSignalEventReceiver signalReceiver;
    private Boolean deliverSignals;
    private String topicPrefix;

    public MqttListener(final MqttClient mqttClient, final CatchingSignalEventReceiver signalReceiver, final String topicPrefix,
            final Boolean deliverSignals) {
        this.client = mqttClient;
        this.signalReceiver = signalReceiver;
        this.topicPrefix = topicPrefix;
        if (deliverSignals == null) {
            this.deliverSignals = Boolean.FALSE;
            logger.warn("MQTT BPMN signal delivery is deactivated. "
                    + "Set mqtt.signals.deliver=true if you want to activate it or set it to false to avoid this message.");
        } else {
            this.deliverSignals = deliverSignals;
        }
    }

    /**
     * Initializes the receiver and register all callbacks.
     */
    public void initializeReceiver() {
        if (this.client.isConnected()) {
            this.client.setCallback(this);
            this.initializeSignalDelivery();
            logger.info("MQTT receiver initialized.");
        } else {
            logger.info("MQTT receiver is not connected.");
        }
    }

    /**
     * Initializes signal delivery. Please set <code>mqtt.deliversignals</code> property to true, to activate it.
     */
    private void initializeSignalDelivery() {
        if (this.deliverSignals) {
            final String fullTopic = topicPrefix + "#";
            try {
                this.client.subscribe(fullTopic);
                logger.info("Subscribed to topic '{}' for signal delivery.", fullTopic);
            } catch (MqttException e) {
                logger.error("Error registering signal listener", e);
            }
        } else {
            logger.info("BPMN Signal delivery is disabled.");
        }
    }

    /**
     * Registers a callback for receiving MQTT messages.
     * 
     * @param topic
     *            topic to listen for messages.
     * @param callback
     *            listener to register.
     */
    public void addListener(final String topic, final MqttCallback callback) {
        logger.info("Adding listener {} to topic '{}'", callback.getClass().getSimpleName(), topic);
        final String fullTopic = this.topicPrefix + topic;
        Collection<MqttCallback> callbacksForTopic = this.listeners.get(fullTopic);
        if (callbacksForTopic == null) {
            callbacksForTopic = new ArrayList<MqttCallback>();
            try {
                this.listeners.put(fullTopic, callbacksForTopic);
                this.client.subscribe(fullTopic);
                logger.info("Subscribed to topic '{}'", fullTopic);
            } catch (MqttException e) {
                logger.error("Error registering listener", e);
            }
        }
        callbacksForTopic.add(callback);
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage message) throws Exception {
        logger.info("Message arrived at '{}' with payload {}", topic, message);
        final Collection<MqttCallback> callbacks = this.listeners.get(topic);
        if (callbacks != null) {
            for (final MqttCallback mqttCallback : callbacks) {
                try {
                    mqttCallback.messageArrived(topic, message);
                } catch (Exception e) {
                    logger.warn("Exception in message processing", e);
                }
            }
        }
        /*
         * Delegates, but cuts-off the prefix.
         */
        if (deliverSignals) {
            signalReceiver.messageArrived(topic.substring(topicPrefix.length()), message);
        }
    }

}
