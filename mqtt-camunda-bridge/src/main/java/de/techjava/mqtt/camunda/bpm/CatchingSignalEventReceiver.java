package de.techjava.mqtt.camunda.bpm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.comm.MqttCallbackAdapter;
import de.techjava.mqtt.camunda.config.Property;

/**
 * Fires BPMN signals on receipt of MQTT messages.
 * <p>
 * The signal name is topic name without the prefix. The value of the MQTT message is stored in the process variable <code>payload</code>
 * </p>
 * 
 * @author Simon Zambrovski
 *
 */
@ApplicationScoped
public class CatchingSignalEventReceiver extends MqttCallbackAdapter {
    public static final String MQTT_SEPARATOR = "/";
    /**
     * Variable name suffix of the stored MQTT message payload.
     */
    public static final String PAYLOAD_PATTERN = "%s.payload";
    /**
     * Variable name suffix of the stored MQTT message topic.
     */
    public static final String TOPIC_PATTERN = "%s.topic";
    private static final Logger LOGGER = LoggerFactory.getLogger(CatchingSignalEventReceiver.class);

    @Inject
    private RuntimeService runtime;

    @Inject
    @Property("mqtt.subsignals.supress")
    private Boolean supressSubSignals;

    @PostConstruct
    public void init() {
        if (supressSubSignals == null) {
            supressSubSignals = Boolean.FALSE;
        }
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage message) throws Exception {
        final String payload = new String(message.getPayload());
        Set<String> signalTopics = getSignalTopics(topic);
        for (String signalTopic : signalTopics) {
            LOGGER.info("Throwing a BPMN signal '{}'", signalTopic);
            Map<String, Object> values = createPayload(signalTopic, topic, payload);
            runtime.signalEventReceived(signalTopic, values);
        }
    }

    /**
     * Create signal-specific payload and topic.
     * 
     * @param signalTopic
     *            signal topic (delivered)
     * @param topic
     *            origin topic
     * @param payload
     *            payload
     * @return map to be put into process payload.
     */
    public Map<String, Object> createPayload(final String signalTopic, final String topic, final String payload) {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(String.format(PAYLOAD_PATTERN, signalTopic), payload);
        values.put(String.format(TOPIC_PATTERN, signalTopic), topic);
        return values;
    }

    /**
     * Determines the singular signal topics and their subtopics.
     * 
     * @param topic
     *            origin topic.
     * @return set of all subtopics.
     */
    public Set<String> getSignalTopics(final String topic) {
        Objects.requireNonNull(topic, "Topic must not be null");
        if (supressSubSignals) {
            final HashSet<String> result = new HashSet<String>();
            result.add(topic);
            return result;
        }
        final String[] topicSegments = topic.split(MQTT_SEPARATOR);
        final Set<String> signalTopics = new HashSet<String>(topicSegments.length);
        StringBuilder builder = new StringBuilder();
        for (int i = topicSegments.length - 1; i >= 0; i--) {
            builder.insert(0, topicSegments[i]);
            signalTopics.add(builder.toString());
            builder.insert(0, MQTT_SEPARATOR);
        }
        return signalTopics;
    }
}
