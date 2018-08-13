package com.altumpoint.easypipe.fileio

import com.altumpoint.easypipe.core.pipes.TypedProperties
import spock.lang.Specification

class FilePublisherSpec extends Specification {


    public static final String TEST_MESSAGE = "test-message"


    def "should publish message to writer"() {
        given: "writer"
        def writer = Mock(FileWriter)

        and: "destination with writer"
        def publisher = new FilePublisher()
        publisher.setWriter(writer)
        def properties = new TypedProperties();
        properties.setProperty(FilePublisher.PROPERTY_ADD_LINE_END, "false")
        publisher.loadProperties(properties)

        when: "publish a message"
        publisher.publish(TEST_MESSAGE)

        then: "writer should be invoked"
        1 * writer.write(TEST_MESSAGE)
    }

    def "should publish message with line ending to writer"() {
        given: "writer"
        def writer = Mock(FileWriter)

        and: "destination with writer"
        def publisher = new FilePublisher()
        publisher.setWriter(writer)

        when: "publish a message"
        publisher.publish(TEST_MESSAGE)

        then: "writer should be invoked"
        1 * writer.write(TEST_MESSAGE + System.getProperty("line.separator"))
    }

    def "should throw exception if cannot create writer in constructor"() {
        given: "exists file with broken path"
        def file = Mock(File)
        file.exists() >> true
        file.getPath() >> {throw new IOException()}

        when: "creates destination"
        new FilePublisher(file)

        then: "IllegalArgumentException should be thrown"
        thrown IllegalArgumentException
    }

    def "should throw exception if cannot create file in constructor"() {
        given: "non exists file with broken creation"
        def file = Mock(File)
        file.exists() >> false
        file.getParentFile() >> Mock(File)
        file.createNewFile() >> false

        when: "creates destination"
        new FilePublisher(file)

        then: "IllegalArgumentException should be thrown"
        thrown IllegalArgumentException
    }

}
