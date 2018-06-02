package com.altumpoint.easypipe.core;

/**
 * Definition object for managing EasyPipe executions.
 *
 * @since 0.1.0
 */
public class EasyPipeInfo {

    private EasyPipe pipe;

    private Thread thread;

    private PipeRunnable runnable;


    public EasyPipe getPipe() {
        return pipe;
    }

    public void setPipe(EasyPipe pipe) {
        this.pipe = pipe;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public PipeRunnable getRunnable() {
        return runnable;
    }

    public void setRunnable(PipeRunnable runnable) {
        this.runnable = runnable;
    }
}
