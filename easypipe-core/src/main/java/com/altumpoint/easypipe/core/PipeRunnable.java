package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.pipes.EasyPipe;

/**
 * Runnable wrapper for running pipes in separate threads.
 *
 * @since 0.1.0
 */
public class PipeRunnable implements Runnable {

    private EasyPipe pipe;


    public PipeRunnable(EasyPipe pipe) {
        this.pipe = pipe;
    }

    /**
     * Run pipe.
     */
    @Override
    public void run() {
        pipe.start();
    }
}
