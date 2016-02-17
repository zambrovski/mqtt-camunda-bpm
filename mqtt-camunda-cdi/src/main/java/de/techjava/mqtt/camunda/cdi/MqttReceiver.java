package de.techjava.mqtt.camunda.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.eclipse.paho.client.mqttv3.MqttClient;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;
import de.techjava.mqtt.camunda.comm.MqttListener;
import de.techjava.mqtt.camunda.config.Property;

@Named
@ApplicationScoped
public class MqttReceiver {

    @Inject
    private MqttClient mqttClient;

    @Inject
    private CatchingSignalEventReceiver signalReceiver;

    @Inject
    @Property("mqtt.signals.deliver")
    private Boolean deliverSignals;

    @Inject
    @Property("mqtt.topic.prefix")
    private String topicPrefix;

    private MqttListener mqttReceiverComponent;

    @PostConstruct
    public void init() {
        mqttReceiverComponent = new MqttListener(mqttClient, signalReceiver, topicPrefix, deliverSignals);
        // initialize receiver
        mqttReceiverComponent.initializeReceiver();
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
