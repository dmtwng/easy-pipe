package com.altumpoint.easypipe.core.steps;

/**
 * Interface for EasyPipe message publishers.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyPublisher<M> {

    void publish(M message);
}
