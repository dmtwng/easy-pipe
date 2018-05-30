package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.stages.EasyPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class LogsPublisher implements EasyPublisher<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogsPublisher.class);

    public void publish(String message) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 250));
        } catch (InterruptedException e) {
            LOGGER.warn("Publisher was interrupted.", e);
            Thread.currentThread().interrupt();
        }
        LOGGER.info("message published: {}", message);
    }

}
