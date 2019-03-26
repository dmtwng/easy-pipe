package com.altumpoint.easypipe.fileio

import com.altumpoint.easypipe.core.pipes.TypedProperties
import spock.lang.Specification
import spock.lang.Timeout
import spock.util.mop.ConfineMetaClassChanges

import java.nio.file.FileSystem
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@ConfineMetaClassChanges([Paths])
class DirectoryListenerSpec extends Specification {

    private path = Mock(Path)
    private fileSystemProvider = Mock(FileSystemProvider)
    private watchService = Mock(WatchService)

    void setup() {
        def fileSystem = Mock(FileSystem)
        fileSystem.provider() >> this.fileSystemProvider
        this.path.getFileSystem() >> fileSystem
        fileSystemProvider.readAttributes(path, "basic:isDirectory", _ as LinkOption[]) >> [
                "isDirectory": true
        ]

        fileSystem.newWatchService() >> this.watchService
        def watchKey = Mock(WatchKey)
        this.watchService.take() >> watchKey
        def watchEvent = Mock(WatchEvent)
        def eventContext = "file event context"
        watchEvent.context() >> eventContext
        watchKey.pollEvents() >> [watchEvent]
    }

    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    def "should consume added file"() {
        given: "attribute is directory is true"
        fileSystemProvider.readAttributes(path, "basic:isDirectory", _ as LinkOption[]) >> [
                "isDirectory": true
        ]

        and: "directory and message consumers"
        def directoryListener = new DirectoryListener(this.path)
        def messageConsumer = Mock(Consumer)
        directoryListener.setMessageConsumer(messageConsumer)
        def properties = new TypedProperties();
        properties.setProperty(DirectoryListener.PROPERTY_POLL_TIMEOUT, "1000")
        directoryListener.loadProperties(properties)

        when: "start consuming"
        directoryListener.start()

        then: "some"
        messageConsumer.accept(_ as String) >> {
            directoryListener.stop()
        }
        true
    }

    def "should fail consumer creation if path is not a directory"() {
        when: "creating new source"
        new DirectoryListener(path).start()

        then: "exception should be thrown"
        fileSystemProvider.readAttributes(path, "basic:isDirectory", _ as LinkOption[]) >> [
                "isDirectory": false
        ]
        thrown IllegalArgumentException
    }

    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    def "should fail start of consuming if creation of watcher failed"() {
        given: "new source"
        def listener = new DirectoryListener(path)

        when: "start consuming"
        listener.start()

        then: "exception should be thrown"
        path.register(_, StandardWatchEventKinds.ENTRY_CREATE) >> {throw new IOException("Failed")}
        thrown IllegalStateException
    }

}
