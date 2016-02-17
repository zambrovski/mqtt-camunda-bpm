package de.techjava.mqtt.camunda.spring;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.techjava.mqtt.camunda.bpm.CatchingSignalEventReceiver;
import de.techjava.mqtt.camunda.bpm.LoggingDelegate;
import de.techjava.mqtt.camunda.bpm.MqttThrowingEventDelegate;

@Configuration
public class CamundaDelegateConfiguration {

    @Autowired
    private RuntimeService runtime;

    @Autowired
    private MqttSender mqttSender;

    @Value("${mqtt.subsignals.supress}")
    private Boolean supressSubSignals;

    @Bean
    public CatchingSignalEventReceiver createCatchingSignalEventReceiver() {
        return new CatchingSignalEventReceiver(runtime, supressSubSignals);
    }

    @Bean
    public LoggingDelegate creagteLoggingDelegate() {
        return new LoggingDelegate();
    }

    @Bean
    public MqttThrowingEventDelegate createThrowingEventDelegate() {
        return new MqttThrowingEventDelegate(mqttSender.getMqttHelper());
    }

}
