package de.techjava.mqtt.camunda.bpm.util;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;

public class MessageHelper {

    private static final String EL_VALUE = "${";

    /**
     * Retrieves message name from the event definition.
     * 
     * @param event
     *            message event sending MQTT messages.
     * 
     * @return message name.
     */
    public static String getTopic(final ThrowEvent event) {
        if (event.getEventDefinitions().isEmpty()) {
            return null;
        }
        final MessageEventDefinition definition = (MessageEventDefinition) event.getEventDefinitions().iterator().next();
        final String topicPattern = definition.getMessage().getName();
        return topicPattern;
    }

    /**
     * Replace variables inside of the topic pattern.
     * 
     * @param execution
     *            delegate execution.
     * @param topicPattern
     *            topic to replace
     * @return topic name with resolved values.
     */
    public static String replaceVariables(final DelegateExecution execution, final String topicPattern) {

        if (topicPattern.contains(EL_VALUE)) {
            StrSubstitutor substitutor = new StrSubstitutor(execution.getVariables());
            return substitutor.replace(topicPattern);
        } else {
            return topicPattern;
        }
    }
}
