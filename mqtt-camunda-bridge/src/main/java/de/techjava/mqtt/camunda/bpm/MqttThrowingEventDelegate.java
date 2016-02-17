package de.techjava.mqtt.camunda.bpm;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.bpm.util.MessageHelper;
import de.techjava.mqtt.camunda.comm.MqttHelper;

/**
 * Delegate for sending MQTT messages using Camunda fields <code>topic</code> and <code>message</code>.
 * 
 * @see https://docs.camunda.org/manual/7.4/user-guide/process-engine/delegation-code/#field-injection for details.
 * @author Simon Zambrovski
 * @author Thorsten Pohl
 */
public class MqttThrowingEventDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttThrowingEventDelegate.class);
    public static final String PAYLOAD_PATTERN = "%s_payload";

    private MqttHelper sender;
    private Expression topic;
    private Expression message;

    public MqttThrowingEventDelegate(final MqttHelper sender) {
        this.sender = sender;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String topicValue = getTopic(execution);
        if (topicValue == null) {
            LOGGER.error("Not throwing any event in {}. Could not determine topic.", execution.getCurrentActivityName());
            return;
        }
        final Object messageValue = getMessagePayload(execution);
        if (messageValue == null) {
            LOGGER.error("Not throwing any event in {}. " + "Designated message payload for topic {} could not be found.", execution.getCurrentActivityName(),
                    topicValue);
            return;
        }
        sender.sendMessage(topicValue, messageValue.toString());
        LOGGER.info("Throwing event topic {} with value {}", topicValue, messageValue);

    }

    /**
     * Retrieves the topic for the message. You can override this method, if you need additional mechanisms.
     * 
     * @param execution
     *            the process execution.
     * @return the topic - if null, no message will be sent.
     */
    protected String getTopic(final DelegateExecution execution) {
        String topicValue = null;
        if (this.topic != null) {
            topicValue = (String) this.topic.getValue(execution);
        }
        if (topicValue == null) {
            // Try to get the topic from the message name including pattern replacement.
            topicValue = MessageHelper.replaceVariables(execution, MessageHelper.getTopic((ThrowEvent) execution.getBpmnModelElementInstance()));
        }
        return topicValue;
    }

    /**
     * Retrieves the payload for the message. You can override this method, if you need additional mechanisms.
     * 
     * @param execution
     *            the process execution.
     * @return the payload - if null, no message will be sent.
     */
    protected Object getMessagePayload(final DelegateExecution execution) {
        Object messageValue = null;
        if (this.message != null) {
            messageValue = this.message.getValue(execution);
        }
        if (messageValue == null) {
            // Resolve the messageValue from process variable {event.id}.payload.
            messageValue = String.valueOf(execution.getVariable(String.format(PAYLOAD_PATTERN, execution.getCurrentActivityId())));
        }
        return messageValue;
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

}
