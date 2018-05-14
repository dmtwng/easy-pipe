package com.altumpoint.easypipe.core.steps;

import com.altumpoint.easypipe.core.EasyPipe;

/**
 * Pipe step for consuming messages and invoking next pipe step.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public class ConsumerStep<M> implements EasyPipeStep<M>, EasyPipe {

    private EasyConsumer<M> consumer;

    private EasyPipeStep<M> nextStep;

    public ConsumerStep(EasyConsumer<M> consumer) {
        this.consumer = consumer;
        this.consumer.setMessageConsumer(this::handle);
    }


    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public void start() {
        this.consumer.start();
    }

    @Override
    public void stop() {
        this.consumer.stop();
    }

    @Override
    public void handle(M message) {
        this.nextStep.handle(message);
    }
}
