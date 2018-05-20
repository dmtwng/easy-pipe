package com.altumpoint.easypipe.core.steps;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;

/**
 * Pipe step for consuming messages and invoking next pipe step.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public class ConsumerStep<M> extends EasyPipeStep<M> {

    private EasyConsumer<M> consumer;

    public ConsumerStep(EasyConsumer<M> consumer, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.consumer = consumer;
        this.consumer.setMessageConsumer(this::handle);
    }


    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        nextStep.handle(message);
        metersStrategy.afterHandling(metersData);
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
