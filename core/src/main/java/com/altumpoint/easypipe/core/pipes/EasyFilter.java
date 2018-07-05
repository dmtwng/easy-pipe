package com.altumpoint.easypipe.core.pipes;

/**
 * Component for messages filtration.
 *
 * Main responsibility is to define messages, which should go into the next
 * stages, and filter out all other messages.
 *
 * @param <M> Type of messages.
 * @since 0.2.0
 */
public interface EasyFilter<M> {

    /**
     * Checks if message should be passed further in pipeline.
     *
     * @param message message to check.
     * @return {@code true} if message should fo into the next stage, {@code false otherwise}
     */
    boolean passes(M message);
}
