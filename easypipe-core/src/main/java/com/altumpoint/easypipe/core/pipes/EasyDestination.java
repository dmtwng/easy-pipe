package com.altumpoint.easypipe.core.pipes;

/**
 * Interface for EasyPipe message publishers.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyDestination<M> extends StageComponent {

    void publish(M message);
}
