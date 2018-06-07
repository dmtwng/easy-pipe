package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.pipes.simple.ConsumerStage
import com.altumpoint.easypipe.core.pipes.simple.SimplePipe
import spock.lang.Specification

class SimplePipeSpec extends Specification {

    private consumer
    private simplePipe

    void setup() {
        consumer = Mock(ConsumerStage)
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
