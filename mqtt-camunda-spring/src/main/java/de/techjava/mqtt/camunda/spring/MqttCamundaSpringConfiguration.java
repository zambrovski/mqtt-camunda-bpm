package de.techjava.mqtt.camunda.spring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/environment.properties")
@ComponentScan
@EnableAutoConfiguration
@Import({ MqttConfiguration.class, CamundaDelegateConfiguration.class })
public class MqttCamundaSpringConfiguration {

}
