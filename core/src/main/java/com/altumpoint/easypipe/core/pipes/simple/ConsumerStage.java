package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyConsumer;

/**
 * Pipe stage for consuming messages and invoking next pipe stage.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public class ConsumerStage<M> extends SimpleStage<M> {

    private EasyConsumer<M> consumer;

    public ConsumerStage(EasyConsumer<M> consumer, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.consumer = consumer;
        this.consumer.setMessageConsumer(this::handle);
    }


    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        nextStage.handle(message);
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
