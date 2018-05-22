package com.altumpoint.easypipe.demo.pipes

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.Appender
import org.slf4j.LoggerFactory
import spock.lang.Specification

class LogsPublisherSpec extends Specification {

    def "should publish to logs"() {
        given:
        def appender = Mock(Appender)
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(LogsPublisher.class).addAppender(appender)

        and:
        def publisher = new LogsPublisher()

        when:
        publisher.publish("message")

        then:
        1 * appender.doAppend(_)
    }
}
