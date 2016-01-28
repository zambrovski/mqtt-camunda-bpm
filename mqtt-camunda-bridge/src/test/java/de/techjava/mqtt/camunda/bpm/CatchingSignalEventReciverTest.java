package de.techjava.mqtt.camunda.bpm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class CatchingSignalEventReciverTest {

    CatchingSignalEventReceiver testee;

    @Before
    public void init() {
        testee = new CatchingSignalEventReceiver();
        testee.init();
    }

    @Test
    public void getSignalTopics() {
        Set<String> signalTopics = testee.getSignalTopics("foo/bar/47/*/12");
        assertEquals(5, signalTopics.size());
        assertTrue(signalTopics.contains("12"));
        assertTrue(signalTopics.contains("*/12"));
        assertTrue(signalTopics.contains("47/*/12"));
        assertTrue(signalTopics.contains("bar/47/*/12"));
        assertTrue(signalTopics.contains("foo/bar/47/*/12"));
    }

    @Test
    public void getSignalOneTopics() {
        Set<String> signalTopics = testee.getSignalTopics("foo");
        assertEquals(1, signalTopics.size());
        assertTrue(signalTopics.contains("foo"));
    }

    @Test
    public void getSignalNoTopics() {
        Set<String> signalTopics = testee.getSignalTopics("/");
        assertEquals(0, signalTopics.size());
    }

    @Test
    public void getSignalNoTopics2() {
        Set<String> signalTopics = testee.getSignalTopics("//");
        assertEquals(0, signalTopics.size());
    }

}
