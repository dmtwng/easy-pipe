package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyPublisher;

/**
 * Pipe stage for message publishing.
 *
 * It also could transfer message to next stage, if it defined.
 *
 * @param <M> type of message.
 * @since 0.1.0
 */
public class PublisherStage<M> extends SimpleStage<M> {

    private EasyPublisher<M> publisher;

    public PublisherStage(EasyPublisher<M> publisher, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.publisher = publisher;
    }


    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        publisher.publish(message);
        metersStrategy.afterHandling(metersData);

        if (nextStage != null) {
            nextStage.handle(message);
        }
    }

}
