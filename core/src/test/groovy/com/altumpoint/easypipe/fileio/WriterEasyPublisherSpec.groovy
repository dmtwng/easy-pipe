package com.altumpoint.easypipe.fileio

import spock.lang.Specification

class WriterEasyPublisherSpec extends Specification {

    public static final String MESSAGE = "test message"


    def "should write publish messages"() {
        given: "writer"
        def writer = Mock(Writer)

        and: "writer easy destination"
        def publisher = new WriterEasyPublisher(writer)

        when: "publish was invoked"
        publisher.publish(MESSAGE)

        then: "message is written in writer"
        1 * writer.write(MESSAGE)
        1 * writer.flush()
    }

    def "should throw exception when write failed"() {
        given: "broken writer"
        def writer = Mock(Writer)
        writer.write(MESSAGE) >> {throw new IOException()}

        and: "writer easy destination"
        def publisher = new WriterEasyPublisher(writer)

        when: "publish was invoked"
        publisher.publish(MESSAGE)

        then: "IllegalStateException should be thrown"
        thrown IllegalStateException
    }

    def "should close writer when it is closed and writer exists"() {
        given: "non null writer"
        def writer = Mock(Writer)

        and: "writer easy destination"
        def publisher = new WriterEasyPublisher(writer)

        when: "close was invoked"
        publisher.close()

        then: "writer should be closed"
        1 * writer.close()

        when: "close was invoked with null writer"
        publisher.setWriter(null)
        publisher.close()

        then: "nothing happens"
        0 * _
    }
}
