package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.pipes.EasySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DoublesConsumer implements EasySource<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoublesConsumer.class);

    private boolean isRunning = false;
    private Consumer<String> messagesConsumer;


    public void start() {
        isRunning = true;
        while (isRunning) {
            String message = String.valueOf(Math.random());
            LOGGER.info("Got message: {}", message);
            messagesConsumer.accept(message);
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
