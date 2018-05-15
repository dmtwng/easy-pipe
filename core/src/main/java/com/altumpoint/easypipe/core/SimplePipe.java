package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.steps.ConsumerStep;

/**
 * Simple single thread pipe with one consumer.
 *
 * @since 0.1.0
 */
public class SimplePipe implements EasyPipe {

    private ConsumerStep consumer;

    public SimplePipe(ConsumerStep consumer) {
        this.consumer = consumer;
    }

    @Override
    public void start() {
        consumer.start();
    }

    @Override
    public void stop() {
        consumer.stop();
    }
}
