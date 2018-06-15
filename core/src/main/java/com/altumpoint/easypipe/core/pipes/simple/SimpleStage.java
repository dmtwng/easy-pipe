package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.meters.MetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyPipeStage;

/**
 * Base stage for simple pipe. Contains next stage, which should be executed
 * after this stage.
 *
 * @param <M> type of messages.
 * @since 0.2.0
 */
public abstract class SimpleStage<M> extends EasyPipeStage {

    protected SimpleStage nextStage;


    public SimpleStage(MetersStrategy metersStrategy) {
        super(metersStrategy);
    }


    /**
     * Set following stage in the pipe.
     *
     * @param pipeStage next pipe.
     */
    public void setNextStage(SimpleStage pipeStage) {
        this.nextStage = pipeStage;
    }

    /**
     * Invoke component of stage to handle message.
     *
     * @param message message to handle.
     */
    public abstract void handle(M message);
}
