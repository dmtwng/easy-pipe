package com.altumpoint.easypipe.fileio;

import com.altumpoint.easypipe.core.steps.EasyConsumer;
import com.altumpoint.easypipe.core.steps.TypedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final String PROPERTY_POLL_TIMEOUT = "pollTimeout";
    public static final int DEFAULT_POLL_TIMEOUT = 1000;

    private Path path;
    private long pollTimeout;

    private boolean isRuning = false;

    private Consumer<String> messageConsumer;


    public DirectoryConsumer(String path) {
        this(Paths.get(path));
    }

    public DirectoryConsumer(Path path) {
        this.path = path;
        this.pollTimeout = pollTimeout;
    }


    @Override
    public void setMessageConsumer(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void setProperties(TypedProperties properties) {
        pollTimeout = properties.getInt(PROPERTY_POLL_TIMEOUT, DEFAULT_POLL_TIMEOUT);
    }

    @Override
    public void start() {
        if (isRuning) {
            return;
        }

        try {
            if (!pathIsDirectory(path)) {
                throw new IOException("Path: " + path.getFileName() + " is not a folder");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to watch a directory: " + path, e);
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
        try (WatchService watcher = path.getFileSystem().newWatchService()) {
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
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

    private boolean pathIsDirectory(Path directoryPath) throws IOException {
        return (Boolean) Files.getAttribute(directoryPath, "basic:isDirectory", LinkOption.NOFOLLOW_LINKS);
    }
}
