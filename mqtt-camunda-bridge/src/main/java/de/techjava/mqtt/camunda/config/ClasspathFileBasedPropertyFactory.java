package de.techjava.mqtt.camunda.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classpath-based property file configuration provider. <br />
 * Will read the <code>environment_$name$.properties</code> from the classpath, where
 * <code>$name$<code> is the environment name specified by the system property <code>environment.name</code>. If no environment name is specified the
 * <code>environment.properties</code> is used.
 * <p>
 * Inspired by <a href="http://codebias.blogspot.de/2013/04/environment-configuration-property.html">Nazar Annagurban</a>
 * </p>
 * 
 * @author Simon Zambrovski
 */
@ApplicationScoped
public class ClasspathFileBasedPropertyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathFileBasedPropertyFactory.class);

    private static final String ENVIRONMENT_NAME_KEY = "environment.name";
    private static final String DEFAULT_PROPS_FILENAME = "environment.properties";
    private static final String PROPS_FILENAME_FORMAT = "environment_%s.properties";

    private Properties environmentProps;

    @PostConstruct
    public void initEnvironmentProps() {
        environmentProps = new Properties();
        final String environmentName = System.getProperty(ENVIRONMENT_NAME_KEY);

        String propsFilename = DEFAULT_PROPS_FILENAME;
        if (environmentName != null) {
            propsFilename = String.format(PROPS_FILENAME_FORMAT, environmentName);
        }
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propsFilename);
        if (inputStream == null) {
            throw new IllegalArgumentException("Properties file for environment " + environmentName + " not found in the classpath.");
        }
        try {
            environmentProps.load(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Wrong properties configuration", e);
        }
    }

    /**
     * Produces the property for requested property value key.
     * <p>
     * This method is not intended top be called by the client.
     * </p>
     * 
     * @param ip
     *            Injection point.
     * @return value which is injected.
     */
    @Produces
    @Property
    public String getPropertyStringValue(InjectionPoint ip) {
        return getRawPropertyValue(ip);
    }

    /**
     * Produces the property for requested property value key.
     * <p>
     * This method is not intended top be called by the client.
     * </p>
     * 
     * @param ip
     *            Injection point.
     * @return value which is injected.
     */
    @Produces
    @Property
    public Integer getPropertyIntegerValue(InjectionPoint ip) {
        final String rawValue = getRawPropertyValue(ip);
        if (rawValue != null) {
            return Integer.parseInt(rawValue);
        }
        return null;
    }

    /**
     * Produces the property for requested property value key.
     * <p>
     * This method is not intended top be called by the client.
     * </p>
     * 
     * @param ip
     *            Injection point.
     * @return value which is injected.
     */
    @Produces
    @Property
    public Boolean getPropertyBooleanValue(InjectionPoint ip) {
        final String rawValue = getRawPropertyValue(ip);
        if (rawValue != null) {
            return Boolean.parseBoolean(rawValue);
        }
        return null;
    }

    private String getRawPropertyValue(InjectionPoint ip) {
        final Property config = ip.getAnnotated().getAnnotation(Property.class);
        final String configKey = config.value();
        if (configKey.isEmpty()) {
            throw new IllegalArgumentException("Property value key is required.");
        }
        final String value = environmentProps.getProperty(configKey);
        if (value == null) {
            LOGGER.warn("Requested property {} not configured.", configKey);
        }
        return value;
    }

}
