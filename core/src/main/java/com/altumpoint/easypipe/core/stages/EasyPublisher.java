package com.altumpoint.easypipe.core.stages;

/**
 * Interface for EasyPipe message publishers.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyPublisher<M> extends StageComponent {

    void publish(M message);
}
