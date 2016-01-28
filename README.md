# MQTT Camunda BPMN
A small library which maps events between MQTT to BPM elements based on Camunda BPM Engine.

## What it does
MQTT is a well adopted protocol in the area of IoT and sensor applications. Camunda BPM provides a light weight, developer-friendly process engine, speaking BPMN 2.0 which allows to create easy-to understand application. The purpose of this library is to bridge the gap between MQTT and BPMN. 

## How it helps
The library provides two abstractions for sending and receiving messages respectively. A message delivered to MQTT topic can be mapped to a Catching Signal Event of BPMN. So the library subscribes to the topic and transmits the payload of the received message to the payload of the BPMN event. This is used to communicate from device to process. A Throwing Message event inside the BPMN process model can be mapped to a message published to a MQTT topic. This is used to communicate from process to device.

## How it works

### MQTT configuration
In order to use the library, a configuration of MQTT protocol has to be supplied. 

	mqtt.broker=tcp://localhost:1883
	mqtt.qos=2
	mqtt.topic.prefix=camunda-bridge

### Initialization of MQTT receiver

- Weld 1.1
- EJB 
- Servlet

### Sending Messages
In order to send a MQTT message, please use a BPMN throwing message event and provide `${mqttThrowingEvent}` as delegateExpression property value. In addition, the MQTT client need the information about the target topic and the message payload. There are two ways to specify those:
#### Using event name and process variable 
You must name the BPMN throwing message event with the topic name (without prefix) and the process execution must contain a variable called `<topic name>.payload`. 

E.g if the prefix is configured to be `camunda-bridge`


	<bpmn:intermediateThrowEvent id="Message_0sdfsf" name="foo/bar/zoo" />
will lead to a message deliver to the topic `camunda-bridge/foo/bar/zoo` with the value taken from the process variable `foo/bar/zoo.payload`.

#### Using Camunda Fields extension
As an alternative you can use the Camunda Fields extensions (see below)

      <bpmn:messageEventDefinition camunda:delegateExpression="${mqttThrowingEvent}">
        <bpmn:extensionElements>
          <camunda:field name="topic" stringValue="foo" />
          <camunda:field name="message">
            <camunda:expression>${barValue}</camunda:expression>
          </camunda:field>
        </bpmn:extensionElements>        
      </bpmn:messageEventDefinition>


### Receiving Signals
In order to convert an incomming MQTT message to a BPMN Signal, the property `mqtt.signals.deliver` must be set to `true`. Then, every message is converted to a signal with the name equals to the topic name without the prefix (and sub-topics, if configured, see below). For example, the following BPMN catching signal event will be triggered on receipt of the message to the topic `<prefix>/foo/bar/zoo`. Both *start process signal event* and *intermediate signal catching event* are supported.

      <bpmn:signalEventDefinition signalRef="Signal_0ohapx3" />
      <bpmn:signal id="Signal_0ohapx3" name="foo/bar/zoo" />

#### Example Scenario

- prefix configuration: `mqtt.topic.prefix=camunda-bridge`
- topic: `camunda-bridge/foo/bar/zoo`
- fired BPMN signals: `zoo`, `bar/zoo`, `foo/bar/zoo`
- payload variables:
  - `zoo.payload` and `zoo.topic` for process instance triggered by the signal `zoo`
  - `bar/zoo.payload` and `bar/zoo.topic` for process instance triggered by the signal `bar/zoo`
  - `foo/bar/zoo.payload` and `foo/bar/zoo.topic` for process instance triggered by the signal `foo/bar/zoo`

You can suppress delivery of sub-signals (`zoo` and `bar/zoo`) by setting the property `mqtt.subsignals.supress` to `true`. 


