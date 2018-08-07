package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.pipes.EasyPipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context class for managing {@link EasyPipe} and holding metadata information.
 *
 * @since 0.3.0
 */
public class PipelineContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineContext.class);


    private EasyPipe pipe;

    private Status status = Status.PENDING;

    private String pipeName;


    /**
     * Starts pipeline in separate thread and changes status to {@code Status.RUNNING}
     * is pipeline started successfully, changes status to {@code Status.FAILED} otherwise.
     *
     * @return {@code true} is pipeline started successfully, {@code false} otherwise.
     */
    public boolean start() {
        Thread pipeThread = new Thread(new PipeRunnable(pipe));
        pipeThread.setUncaughtExceptionHandler(new PipeThreadExceptionHandler());
        pipeThread.start();
        status = PipelineContext.Status.RUNNING;
        return true;
    }

    /**
     * Stops pipeline and changes status to {@code Status.PENDING}
     * is pipeline stopped successfully, changes status to {@code Status.FAILED} otherwise.
     *
     * @return {@code true} is pipeline stopped successfully, {@code false} otherwise.
     */
    public boolean stop() {
        try {
            pipe.stop();
            status = PipelineContext.Status.PENDING;
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start EasyPipe with name {0}", pipeName, e);
            status = PipelineContext.Status.FAILED;
            return false;
        }
        return true;
    }


    public EasyPipe getPipe() {
        return pipe;
    }

    public void setPipe(EasyPipe pipe) {
        this.pipe = pipe;
    }

    public Status getStatus() {
        return status;
    }

    public String getPipeName() {
        return pipeName;
    }

    public void setPipeName(String pipeName) {
        this.pipeName = pipeName;
    }

    public enum Status {
        PENDING("Pending"),
        RUNNING("Running"),
        FAILED("Failed");

        String name;

        Status(String name) {
            this.name = name;
        }
    }


    /**
     * Exception handler for pipes threads.
     * In case of exception, changes status of pipe to {@code FAILED}.
     */
    private class PipeThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public synchronized void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Pipe {} failed", pipeName, e);

            status = PipelineContext.Status.FAILED;
        }
    }
}
