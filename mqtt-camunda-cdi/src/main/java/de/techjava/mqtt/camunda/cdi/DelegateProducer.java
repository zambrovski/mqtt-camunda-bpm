package de.techjava.mqtt.camunda.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;
import de.techjava.mqtt.camunda.bpm.LoggingDelegate;
import de.techjava.mqtt.camunda.bpm.MqttThrowingEventDelegate;
import de.techjava.mqtt.camunda.config.Property;

@ApplicationScoped
public class DelegateProducer {

    @Inject
    private RuntimeService runtime;

    @Inject
    private MqttSender mqttSender;

    @Inject
    @Property("mqtt.subsignals.supress")
    private Boolean supressSubSignals;

    private CatchingSignalEventReceiver catchingSignalEventReceiver;
    private LoggingDelegate loggingDelegate;
    private MqttThrowingEventDelegate mqttThrowingEventDelegate;

    @PostConstruct()
    public void init() {
        catchingSignalEventReceiver = new CatchingSignalEventReceiver(runtime, supressSubSignals);
        loggingDelegate = new LoggingDelegate();
        mqttThrowingEventDelegate = new MqttThrowingEventDelegate(mqttSender.getMqttHelper());
    }

    @Produces
    public CatchingSignalEventReceiver createCatchingSignalEventReceiver() {
        return catchingSignalEventReceiver;
    }

    @Produces
    public LoggingDelegate creagteLoggingDelegate() {
        return loggingDelegate;
    }

    @Produces
    public MqttThrowingEventDelegate createThrowingEventDelegate() {
        return mqttThrowingEventDelegate;
    }

}
