package com.altumpoint.easypipe.fileio;

import com.altumpoint.easypipe.core.steps.EasyConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

/**
 * Easy consumer for consuming all created files in specified directory.
 * It is possible to specify timeout in milliseconds for checking directory for new files,
 * by default this value equals {@code 1000}.
 *
 * @since 0.2.0
 */
public class DirectoryConsumer implements EasyConsumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryConsumer.class);

    private static final int DEFAULT_POLL_TIMEOUT = 1000;

    private String path;
    private long pollTimeout;

    private boolean isRuning = false;

    private Consumer<String> messageConsumer;


    public DirectoryConsumer(String path) {
        this(path, DEFAULT_POLL_TIMEOUT);
    }

    public DirectoryConsumer(String path, long pollTimeout) {
        this.path = path;
        this.pollTimeout = pollTimeout;
    }


    @Override
    public void setMessageConsumer(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void start() {
        if (isRuning) {
            return;
        }

        LOGGER.info("Starting watching directory {}", path);
        isRuning = true;
        watchDirectory();
    }

    @Override
    public void stop() {
        isRuning = false;
        LOGGER.info("Stopping watching directory {}", path);
    }


    private void watchDirectory() {
        Path directoryPath = Paths.get(path);
        checkItIsFolder(directoryPath);

        try (WatchService watcher = directoryPath.getFileSystem().newWatchService()) {
            directoryPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey directoryWatchKey = watcher.take();

            while (isRuning) {
                for (WatchEvent event : directoryWatchKey.pollEvents()) {
                    messageConsumer.accept(path + "/" + event.context());
                }
                Thread.sleep(pollTimeout);
            }
        } catch (IOException|InterruptedException e) {
            throw new IllegalStateException("Failed to watch directory: " + path, e);
        }
    }

    private void checkItIsFolder(Path directoryPath) {
        try {
            if (!(Boolean) Files.getAttribute(directoryPath, "basic:isDirectory", LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("Path: " + directoryPath + " is not a folder");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Specified path is not a directory: " + path, e);
        }
    }
}
