package com.altumpoint.easypipe.fileio;

import com.altumpoint.easypipe.core.steps.TypedProperties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes all received events into a file.
 * By default, publisher adds line ending after each message. It is possible
 * to disable it by passing an argument into constructor.
 *
 * In case if specified path is not exists, it will be created.
 *
 * @since 0.2.0
 */
public class FileEasyPublisher extends WriterEasyPublisher {

    public static final String PROPERTY_ADD_LINE_END = "addLineEnd";
    public static final boolean DEFAULT_ADD_LINE_END = true;

    private boolean addLineEnding = DEFAULT_ADD_LINE_END;


    public FileEasyPublisher() {
    }

    public FileEasyPublisher(String path) {
        this(new File(path));
    }

    public FileEasyPublisher(File file) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new IOException("Could not create file for a specified path " + file.getAbsolutePath() + '.');
                }
            }

            setWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Could not create writer for a specified path " + file.getAbsolutePath() + '.', e);
        }
    }


    @Override
    public void publish(String message) {
        if (addLineEnding) {
            super.publish(message + System.getProperty("line.separator"));
        } else {
            super.publish(message);
        }
    }

    public void setProperties(TypedProperties properties) {
        addLineEnding = properties.getBoolean(PROPERTY_ADD_LINE_END, DEFAULT_ADD_LINE_END);
    }

}
