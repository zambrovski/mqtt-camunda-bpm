package de.techjava.mqtt.camunda.bpm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import de.techjava.mqtt.camunda.bpm.util.SignalHelper;

public class SignalHelperTest {

    @Test
    public void getSignalTopics() {
        Set<String> signalTopics = SignalHelper.getSignalTopics("foo/bar/47/*/12", false);
        assertEquals(5, signalTopics.size());
        assertTrue(signalTopics.contains("12"));
        assertTrue(signalTopics.contains("*/12"));
        assertTrue(signalTopics.contains("47/*/12"));
        assertTrue(signalTopics.contains("bar/47/*/12"));
        assertTrue(signalTopics.contains("foo/bar/47/*/12"));
    }

    @Test
    public void getSignalOneTopics() {
        Set<String> signalTopics = SignalHelper.getSignalTopics("foo", false);
        assertEquals(1, signalTopics.size());
        assertTrue(signalTopics.contains("foo"));
    }

    @Test
    public void getSignalNoTopics() {
        Set<String> signalTopics = SignalHelper.getSignalTopics("/", false);
        assertEquals(0, signalTopics.size());
    }

    @Test
    public void getSignalNoTopics2() {
        Set<String> signalTopics = SignalHelper.getSignalTopics("//", false);
        assertEquals(0, signalTopics.size());
    }

}
