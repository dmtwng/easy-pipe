package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.pipes.EasyPipe;

/**
 * Wrapper object for saving EasyPipe metadata.
 *
 * @since 0.1.0
 */
public class EasyPipeInfo {

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
