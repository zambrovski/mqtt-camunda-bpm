package de.techjava.mqtt.camunda.comm;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttCallbackAdapter implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        
    }

}
