package com.altumpoint.easypipe.fileio

import spock.lang.Specification

class FileEasyPublisherSpec extends Specification {


    public static final String TEST_MESSAGE = "test-message"


    def "should publish message to writer"() {
        given: "writer"
        def writer = Mock(FileWriter)

        and: "publisher with writer"
        def publisher = new FileEasyPublisher()
        publisher.setWriter(writer)
        publisher.setAddLineEnding(false)

        when: "publish a message"
        publisher.publish(TEST_MESSAGE)

        then: "writer should be invoked"
        1 * writer.write(TEST_MESSAGE)
    }

    def "should publish message with line ending to writer"() {
        given: "writer"
        def writer = Mock(FileWriter)

        and: "publisher with writer"
        def publisher = new FileEasyPublisher()
        publisher.setWriter(writer)
        publisher.setAddLineEnding(true)

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

        when: "creates publisher"
        new FileEasyPublisher(file, true)

        then: "IllegalArgumentException should be thrown"
        thrown IllegalArgumentException
    }

    def "should throw exception if cannot create file in constructor"() {
        given: "non exists file with broken creation"
        def file = Mock(File)
        file.exists() >> false
        file.getParentFile() >> Mock(File)
        file.createNewFile() >> false

        when: "creates publisher"
        new FileEasyPublisher(file, true)

        then: "IllegalArgumentException should be thrown"
        thrown IllegalArgumentException
    }

}
