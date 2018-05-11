package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.steps.EasyConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DoublesConsumer implements EasyConsumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoublesConsumer.class);

    private boolean isRunning = false;
    private Consumer<String> messagesConsumer;


    public void start() {
        isRunning = true;
        try {
            while (isRunning) {
                Thread.sleep(500);
                String message = String.valueOf(Math.random());
                LOGGER.info("Got message: {}", message);
                messagesConsumer.accept(message);
            }
        } catch (InterruptedException e) {
            LOGGER.info("Generator was interrupted");
        }
    }

    public void stop() {
        isRunning = false;
        LOGGER.info("Going to stop consuming");
    }

    @Override
    public void setMessageConsumer(Consumer<String> messageConsumer) {
        this.messagesConsumer = messageConsumer;
    }

}
