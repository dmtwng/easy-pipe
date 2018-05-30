package com.altumpoint.easypipe.fileio

import com.altumpoint.easypipe.core.stages.TypedProperties
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
class DirectoryConsumerSpec extends Specification {

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
        def directoryConsumer = new DirectoryConsumer(this.path)
        def messageConsumer = Mock(Consumer)
        directoryConsumer.setMessageConsumer(messageConsumer)
        def properties = new TypedProperties();
        properties.setProperty(DirectoryConsumer.PROPERTY_POLL_TIMEOUT, "1000")
        directoryConsumer.loadProperties(properties)

        when: "start consuming"
        directoryConsumer.start()

        then: "some"
        messageConsumer.accept(_ as String) >> {
            directoryConsumer.stop()
        }
        true
    }

    def "should fail consumer creation if path is not a directory"() {
        when: "creating new consumer"
        new DirectoryConsumer(path).start()

        then: "exception should be thrown"
        fileSystemProvider.readAttributes(path, "basic:isDirectory", _ as LinkOption[]) >> [
                "isDirectory": false
        ]
        thrown IllegalArgumentException
    }

    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    def "should fail start of consuming if creation of watcher failed"() {
        given: "new consumer"
        def consumer = new DirectoryConsumer(path)

        when: "start consuming"
        consumer.start()

        then: "exception should be thrown"
        path.register(_, StandardWatchEventKinds.ENTRY_CREATE) >> {throw new IOException("Failed")}
        thrown IllegalStateException
    }

}
