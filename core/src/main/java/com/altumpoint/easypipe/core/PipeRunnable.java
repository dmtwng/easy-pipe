package com.altumpoint.easypipe.core;

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
     * Terminate pipe.
     */
    public void terminate() {
        pipe.stop();
    }

    /**
     * Run pipe.
     */
    @Override
    public void run() {
        pipe.start();
    }
}
