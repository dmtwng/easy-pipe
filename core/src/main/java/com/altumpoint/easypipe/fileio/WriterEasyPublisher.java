package com.altumpoint.easypipe.fileio;

import com.altumpoint.easypipe.core.stages.EasyPublisher;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

/**
 * Writes messages into specified {@link Writer}.
 *
 * @since 0.2.0
 */
public class WriterEasyPublisher implements EasyPublisher<String>, Closeable {

    private Writer writer;


    public WriterEasyPublisher() {
    }

    public WriterEasyPublisher(Writer writer) {
        this.writer = writer;
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void publish(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write a message to writer.", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
