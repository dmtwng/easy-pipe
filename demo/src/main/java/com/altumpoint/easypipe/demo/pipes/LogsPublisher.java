package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.steps.EasyPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogsPublisher implements EasyPublisher<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsPublisher.class);

    public void publish(String message) {
        LOGGER.info("message published: {}", message);
    }

}
