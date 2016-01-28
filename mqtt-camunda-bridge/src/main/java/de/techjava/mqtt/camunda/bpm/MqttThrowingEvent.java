package de.techjava.mqtt.camunda.bpm;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.comm.MqttSender;

/**
 * Delegate for sending MQTT messages using Camunda fields <code>topic</code> and <code>message</code>.
 * @see https://docs.camunda.org/manual/7.4/user-guide/process-engine/delegation-code/#field-injection for details.
 * @author Simon Zambrovski
 * @author Thorsten Pohl
 */
@Named
public class MqttThrowingEvent implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttThrowingEvent.class);

    public static final String PAYLOAD_PATTERN = "%s.payload";

    @Inject
    private MqttSender sender;

    private Expression topic;
    private Expression message;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String topicValue = (String)topic.getValue(execution);

        if (topicValue == null) {
            // Try to get the topic from the message name including pattern replacement.
            String topicPattern = getMessageName(execution);
            topicValue = replaceVariables(execution, topicPattern);
        }

        Object messageValue = message.getValue(execution);
        final String variableName = String.format(PAYLOAD_PATTERN, execution.getCurrentActivityId());
        if (messageValue == null) {
            // Resolve the messageValue from process variable {event.id}.payload.

            messageValue = String.valueOf(execution.getVariable(variableName));
        }

        if (messageValue != null) {
            sender.sendMessage(topicValue, messageValue.toString());
            LOGGER.info("Throwing event topic {} with value {}", topicValue, messageValue);
        } else {
            LOGGER.error("Not throwing any event in {}. " + "Designated value for topic {} was not set neither per field nor in the process variable {}.",
                    execution.getCurrentActivityName(), topicValue, variableName);
        }
    }

    public Expression getTopic() {
        return topic;
    }

    public void setTopic(Expression topic) {
        this.topic = topic;
    }

    public Expression getMessage() {
        return message;
    }

    public void setMessage(Expression message) {
        this.message = message;
    }

    public static String replaceVariables(DelegateExecution execution, String topicPattern) {

        if (topicPattern.contains("${")) {
            StrSubstitutor substitutor = new StrSubstitutor(execution.getVariables());
            return substitutor.replace(topicPattern);
        } else {
            return topicPattern;
        }

    }

    public static String getMessageName(DelegateExecution execution) {
        try {
            IntermediateThrowEvent event = (IntermediateThrowEvent)execution.getBpmnModelElementInstance();
            MessageEventDefinition definition = (MessageEventDefinition)event.getEventDefinitions().iterator().next();
            String topicPattern = definition.getMessage().getName();
            return topicPattern;
        } catch (Exception e) {
            LOGGER.info("Cannot get the Message Name.", e);
            return null;
        }
    }

}
