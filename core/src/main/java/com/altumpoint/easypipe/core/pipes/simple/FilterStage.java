package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyFilter;

/**
 * Pipe stage for messages filtering.
 *
 * Sends messages into next stage if filter returns {@code true}, skips otherwise.
 *
 * @param <M> type of message.
 * @since 0.2.0
 */
public class FilterStage<M> extends SimpleStage<M> {

    private EasyFilter<M> filter;

    public FilterStage(EasyFilter<M> filter, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.filter = filter;
    }

    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        boolean passes = filter.passes(message);
        metersStrategy.afterHandling(metersData);

        if (nextStage != null && passes) {
            nextStage.handle(message);
        }
    }
}
