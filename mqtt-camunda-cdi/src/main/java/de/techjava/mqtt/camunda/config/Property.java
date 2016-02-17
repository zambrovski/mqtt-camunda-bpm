package de.techjava.mqtt.camunda.config;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotates an element that represents a configuration property, which is injected from the environment property provider.
 * 
 * @author Simon Zambrovski
 * <p>Inspired by <a href="http://codebias.blogspot.de/2013/04/environment-configuration-property.html">Nazar Annagurban</a>
 */
@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
@Documented
public @interface Property {
    @Nonbinding
    String value() default "";
}
