package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.pipes.EasyPipe;

/**
 * Context class for managing {@link EasyPipe} and holding metadata information.
 *
 * @since 0.3.0
 */
public class PipelineContext {

    private EasyPipe pipe;

    private Status status;


    public EasyPipe getPipe() {
        return pipe;
    }

    public void setPipe(EasyPipe pipe) {
        this.pipe = pipe;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
}
