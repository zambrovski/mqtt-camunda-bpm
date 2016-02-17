package de.techjava.mqtt.camunda.config;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalPackages;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalPackages(Property.class)
public class ClasspathFileBasedPropertyFactoryTest {

    @Inject
    @Property("myValue")
    String stringValue;

    @Inject
    @Property("myValueInteger")
    Integer integerValue;

    @Inject
    @Property("myValueBoolean")
    Boolean booleanValue;

    @Inject
    @Property("noValue")
    String noValue;

    @Inject
    @Property("missingValue")
    String missingValue;

    
    @Test
    public void testStringValue() {
        assertEquals("foo", stringValue);
    }

    @Test
    public void testNoValue() {
        assertEquals("", noValue);
    }

    @Test
    public void testNullValue() {
        assertNull(missingValue);
    }

    @Test
    public void testIntegerValue() {
        assertEquals(Integer.valueOf(4711), integerValue);
    }

    @Test
    public void testBooleanValue() {
        assertEquals(true, booleanValue);
    }

}
