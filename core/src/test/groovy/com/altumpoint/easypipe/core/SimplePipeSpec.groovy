package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.steps.ConsumerStep
import spock.lang.Specification

class SimplePipeSpec extends Specification {

    def consumer
    def simplePipe

    void setup() {
        consumer = Mock(ConsumerStep)
        simplePipe = new SimplePipe(consumer)
    }

    def "pipe should start consuming"() {
        when:
        simplePipe.start()

        then:
        1 * consumer.start()
    }

    def "pipe should stop consuming"() {
        when:
        simplePipe.stop()

        then:
        1 * consumer.stop()
    }
}
