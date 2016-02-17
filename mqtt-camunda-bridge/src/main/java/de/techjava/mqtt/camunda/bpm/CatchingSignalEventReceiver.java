package de.techjava.mqtt.camunda.bpm;

import org.camunda.bpm.engine.RuntimeService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.techjava.mqtt.camunda.bpm.util.SignalHelper;
import de.techjava.mqtt.camunda.comm.MqttCallbackAdapter;

/**
 * Fires BPMN signals on receipt of MQTT messages.
 * <p>
 * The signal name is topic name without the prefix. The value of the MQTT message is stored in the process variable <code>payload</code>
 * </p>
 * 
 * @author Simon Zambrovski
 */
public class CatchingSignalEventReceiver extends MqttCallbackAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatchingSignalEventReceiver.class);

    private RuntimeService runtime;
    private Boolean supressSubSignals;

    public CatchingSignalEventReceiver(final RuntimeService runtime, final Boolean supressSubSignal) {
        this.runtime = runtime;
        if (supressSubSignals == null) {
            this.supressSubSignals = Boolean.FALSE;
        } else {
            this.supressSubSignals = supressSubSignal;
        }
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage message) throws Exception {
        final String payload = new String(message.getPayload());
        for (final String signalTopic : SignalHelper.getSignalTopics(topic, supressSubSignals)) {
            LOGGER.info("Throwing a BPMN signal '{}'", signalTopic);
            runtime.signalEventReceived(signalTopic, SignalHelper.createPayload(signalTopic, topic, payload));
        }
    }

}
