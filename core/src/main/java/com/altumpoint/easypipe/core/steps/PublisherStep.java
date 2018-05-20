package com.altumpoint.easypipe.core.steps;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;

/**
 * Pipe step for message publishing.
 *
 * It also could transfer message to next step, if it defined.
 *
 * @param <M> type of message.
 * @since 0.1.0
 */
public class PublisherStep<M> extends EasyPipeStep<M> {

    private EasyPublisher<M> publisher;

    public PublisherStep(EasyPublisher<M> publisher, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.publisher = publisher;
    }


    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        publisher.publish(message);
        metersStrategy.afterHandling(metersData);

        if (nextStep != null) {
            nextStep.handle(message);
        }
    }

}
