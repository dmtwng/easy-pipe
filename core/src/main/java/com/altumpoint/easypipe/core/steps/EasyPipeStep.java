package com.altumpoint.easypipe.core.steps;

/**
 * Interface for EasyPipe step instance.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyPipeStep<M> {

    void handle(M message);

    void setNextStep(EasyPipeStep nextStep);
}
