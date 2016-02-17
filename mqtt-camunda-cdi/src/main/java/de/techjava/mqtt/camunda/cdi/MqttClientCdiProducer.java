package de.techjava.mqtt.camunda.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.eclipse.paho.client.mqttv3.MqttClient;

import de.techjava.mqtt.camunda.comm.MqttClientFactory;
import de.techjava.mqtt.camunda.config.Property;

/**
 * CDI Producer for MQTT client
 * 
 * @author Simon Zambrovski
 */
public class MqttClientCdiProducer {

    @Inject
    @Property("mqtt.broker")
    private String broker;

    @Inject
    @Property("mqtt.client.id")
    private String clientId;

    @Inject
    @Property("mqtt.disabled")
    private Boolean disabled;

    private MqttClientFactory mqttClientFactory;

    @PostConstruct
    public void init() {
        mqttClientFactory = new MqttClientFactory(broker, clientId, disabled);
    }

    @Produces
    public MqttClient createClient() {
        return mqttClientFactory.createMqttClient();
    }

    public void destroyClient(@Disposes final MqttClient client) {
        mqttClientFactory.destroy(client);
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
