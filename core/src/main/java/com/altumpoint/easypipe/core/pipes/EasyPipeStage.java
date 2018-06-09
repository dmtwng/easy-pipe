package com.altumpoint.easypipe.core.pipes;

import com.altumpoint.easypipe.core.meters.MetersStrategy;

/**
 * Interface for EasyPipe stage instance.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public abstract class EasyPipeStage<M> {

    protected final MetersStrategy metersStrategy;

    public EasyPipeStage(MetersStrategy metersStrategy) {
        this.metersStrategy = metersStrategy;
    }

}
