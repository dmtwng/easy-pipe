package com.altumpoint.easypipe.core.steps;

/**
 * Pipe step for consuming messages and invoking next pipe step.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public class ConsumerStep<M> implements EasyPipeStep<M> {

    private EasyConsumer<M> consumer;

    private EasyPipeStep<M> nextStep;

    public ConsumerStep(EasyConsumer<M> consumer) {
        this.consumer = consumer;
        this.consumer.setMessageConsumer(this::handle);
    }


    @Override
    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public void handle(M message) {
        nextStep.handle(message);
    }

    /**
     * Starts consuming.
     */
    public void start() {
        this.consumer.start();
    }

    /**
     * Stops consuming.
     */
    public void stop() {
        this.consumer.stop();
    }
}
