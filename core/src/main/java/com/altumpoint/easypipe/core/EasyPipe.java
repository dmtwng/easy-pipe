package com.altumpoint.easypipe.core;

/**
 * Main interface of EasyPipe instance - one particular stream of data.
 *
 * @since 0.1.0
 */
public interface EasyPipe {

    /**
     * Run pipeline.
     */
    void start();

    /**
     * Stop pipeline.
     */
    void stop();
}
