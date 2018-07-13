package com.altumpoint.easypipe.core.pipes.simple

import spock.lang.Specification

class SimplePipeSpec extends Specification {

    private consumer
    private simplePipe

    void setup() {
        consumer = Mock(SourceStage)
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
