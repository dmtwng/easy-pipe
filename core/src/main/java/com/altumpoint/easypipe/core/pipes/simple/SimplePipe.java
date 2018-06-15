package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.pipes.EasyPipe;

/**
 * Simple single thread pipe with one consumer.
 *
 * @since 0.1.0
 */
public class SimplePipe implements EasyPipe {

    private SourceStage consumer;

    public SimplePipe(SourceStage consumer) {
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
