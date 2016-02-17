package de.techjava.mqtt.camunda.bpm.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.camunda.spin.plugin.variable.value.builder.JsonValueBuilder;

public class SignalHelper {

    public static final String MQTT_SEPARATOR = "/";
    /**
     * Variable name suffix of the stored MQTT message payload.
     */
    public static final String PAYLOAD_PATTERN = "%s_payload";
    /**
     * Variable name suffix of the stored MQTT message topic.
     */
    public static final String TOPIC_PATTERN = "%s_topic";

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
    public static Map<String, Object> createPayload(final String signalTopic, final String topic, final String payload) {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(String.format(PAYLOAD_PATTERN, signalTopic), convertPayload(payload));
        values.put(String.format(TOPIC_PATTERN, signalTopic), topic);
        return values;
    }

    /**
     * Converts the payload to a Camunda JSON Object.
     * 
     * @param payload
     *            the raw payload string.
     * @return a Camunda JSON Object.
     */
    public static Object convertPayload(final String payload) {
        final JsonValueBuilder builder = SpinValues.jsonValue(payload);
        final JsonValue jsonValue = builder.create();
        return jsonValue;
    }

    /**
     * Determines the singular signal topics and their subtopics.
     * 
     * @param topic
     *            origin topic.
     * @return set of all subtopics.
     */
    public static Set<String> getSignalTopics(final String topic, final Boolean supressSubSignals) {
        Objects.requireNonNull(topic, "Topic must not be null");
        if (supressSubSignals) {
            return Collections.singleton(topic);
        }
        final String[] topicSegments = topic.split(MQTT_SEPARATOR);
        final Set<String> signalTopics = new HashSet<String>(topicSegments.length);
        final StringBuilder builder = new StringBuilder();
        for (int i = topicSegments.length - 1; i >= 0; i--) {
            builder.insert(0, topicSegments[i]);
            signalTopics.add(builder.toString());
            builder.insert(0, MQTT_SEPARATOR);
        }
        return signalTopics;
    }
}
