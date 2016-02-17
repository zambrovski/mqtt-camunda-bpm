package de.techjava.mqtt.camunda.bpm;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic logging delegate.
 * 
 * @author Simon Zambrovski
 */
public class LoggingDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingDelegate.class);

    @Override
    public void execute(final DelegateExecution delegateExecution) throws Exception {

        LOGGER.info("Execution {} is in activity {}", delegateExecution.getId(), delegateExecution.getCurrentActivityName());
        for (String name : delegateExecution.getVariableNames()) {
            LOGGER.info("Variable {} has value '{}'", name, delegateExecution.getVariable(name));
        }
    }
}
