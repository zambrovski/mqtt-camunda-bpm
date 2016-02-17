package de.techjava.mqtt.camunda.spring;

import static org.junit.Assert.assertNotNull;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MqttCamundaSpringConfiguration.class })
public class MqttCamundaSpringConfigurationTest {

    @Autowired
    MqttClient client;

    @Ignore
    @Test
    public void testComponents() {
        assertNotNull(client);
    }
}
