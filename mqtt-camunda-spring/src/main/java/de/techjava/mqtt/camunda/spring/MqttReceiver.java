package de.techjava.mqtt.camunda.spring;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;
import de.techjava.mqtt.camunda.comm.MqttListener;

@Component
public class MqttReceiver {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private CatchingSignalEventReceiver signalReceiver;

    @Value("${mqtt.signals.deliver}")
    private Boolean deliverSignals;

    @Value("${mqtt.topic.prefix}")
    private String topicPrefix;

    private MqttListener mqttReceiverComponent;

    @PostConstruct
    public void init() {
        mqttReceiverComponent = new MqttListener(mqttClient, signalReceiver, topicPrefix, deliverSignals);
        // initialize receiver
        mqttReceiverComponent.initializeReceiver();
    }
}
