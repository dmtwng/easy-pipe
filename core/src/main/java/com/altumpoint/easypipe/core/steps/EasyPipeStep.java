package com.altumpoint.easypipe.core.steps;

import com.altumpoint.easypipe.core.meters.MetersStrategy;

/**
 * Interface for EasyPipe step instance.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public abstract class EasyPipeStep<M> {

    protected final MetersStrategy metersStrategy;

    protected EasyPipeStep nextStep;


    public EasyPipeStep(MetersStrategy metersStrategy) {
        this.metersStrategy = metersStrategy;
    }


    public abstract void handle(M message);

    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }
}
