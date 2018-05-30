package com.altumpoint.easypipe.core.steps;

import com.altumpoint.easypipe.core.EasyPipe;

import java.util.function.Consumer;

/**
 * Interface for EasyPipe message consumers.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyConsumer<M> extends StageComponent {

    void setMessageConsumer(Consumer<M> messageConsumer);

    /**
     * Starts consuming.
     */
    void start();

    /**
     * Stops consuming.
     */
    void stop();
}
