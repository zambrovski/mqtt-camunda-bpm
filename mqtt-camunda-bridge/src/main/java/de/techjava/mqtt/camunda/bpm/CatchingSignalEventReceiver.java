package de.techjava.mqtt.camunda.bpm;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.comm.MqttCallbackAdapter;

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
    /**
     * Variable name of the stored MQTT message payload.
     */
    public static final String PAYLOAD = "payload";
    private static final Logger LOGGER = LoggerFactory.getLogger(CatchingSignalEventReceiver.class);

    @Inject
    private RuntimeService runtime;

    @Override
    public void messageArrived(final String topic, final MqttMessage message) throws Exception {
        LOGGER.info("Throwing a BPMN signal '{}'", topic);
        final String payload = new String(message.getPayload());
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(PAYLOAD, payload);
        runtime.signalEventReceived(topic, values);
    }
}
