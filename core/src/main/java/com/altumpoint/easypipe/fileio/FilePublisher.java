package com.altumpoint.easypipe.fileio;

import com.altumpoint.easypipe.core.steps.EasyPublisher;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes all received events into a file.
 * By default, publisher adds line ending after each message. It is possible
 * to disable it by passing an argument into constructor.
 *
 * @since 0.2.0
 */
public class FilePublisher implements EasyPublisher<String>, Closeable {

    private FileWriter fileWriter;

    private boolean addLineEnding;


    public FilePublisher(String path) {
        this(path, true);
    }

    public FilePublisher(String path, boolean addLineEnding) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            this.fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create writer for a specified path " + path + '.', e);
        }

        this.addLineEnding = addLineEnding;
    }

    @Override
    public void publish(String message) {
        try {
            fileWriter.write(message);
            if (addLineEnding) {
                fileWriter.write(System.getProperty("line.separator"));
            }
            fileWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write message to a file.", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
