package com.altumpoint.easypipe.demo.pipes

import spock.lang.Specification

import java.util.function.Consumer

class DoublesConsumerSpec extends Specification {

    def "start and consume message"() {
        given:
        def doublesConsumer = new DoublesConsumer();
        def consumer = Mock(Consumer)
        def stopped = false
        consumer.accept(_ as String) >> {
            doublesConsumer.stop()
            stopped = true
        }
        doublesConsumer.setMessageConsumer(consumer)

        when:
        doublesConsumer.start()

        then:
        stopped
    }

}
