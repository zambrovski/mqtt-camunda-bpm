package de.techjava.mqtt.camunda.comm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;
import de.techjava.mqtt.camunda.config.Property;

/**
 * MqttListener Bean. Manages all subscriptions to MQTT.
 * 
 * @author Thorsten Pohl
 * @author Simon Zambrovski
 */
@ApplicationScoped
@Named
public class MqttReceiver extends MqttCallbackAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MqttReceiver.class);
    private Map<String, Collection<MqttCallback>> listeners = new HashMap<String, Collection<MqttCallback>>();

    @Inject
    private MqttClient client;

    @Inject
    private CatchingSignalEventReceiver signalReceiver;

    @Inject
    @Property("mqtt.qos")
    private Integer qos;

    @Inject
    @Property("mqtt.signals.deliver")
    private Boolean deliverSignals;

    @Inject
    @Property("mqtt.topic.prefix")
    private String topicPrefix;

    @PostConstruct
    public void init() {
        if (this.deliverSignals == null) {
            this.deliverSignals = Boolean.FALSE;
            logger.warn("MQTT BPMN signal delivery is deactivated. "
                    + "Set mqtt.topic.prefix=true if you want to activate it or set it to false to avoid this message.");
        }
        if (this.client.isConnected()) {
            this.client.setCallback(this);
            this.initializeSignalDelivery();
            logger.info("MQTT receiver initialized.");
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

    /**
     * This methods starts up the receiver on application startup.
     * 
     * @param payload
     *            servlet context.
     */
    public void processApplicationScopedInit(@Observes @Initialized(ApplicationScoped.class) ServletContext payload) {
        // do nothing.
    }

}
