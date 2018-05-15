package com.altumpoint.easypipe.core.steps

import spock.lang.Specification

class ConsumerStepSpec extends Specification {

    private consumer
    private consumerStep
    private nextStep

    void setup() {
        consumer = Mock(EasyConsumer)
        consumerStep = new ConsumerStep<String>(consumer)
        nextStep = Mock(EasyPipeStep)
        consumerStep.setNextStep(nextStep)
    }

    void cleanup() {
    }


    def "should start consuming"() {
        when:
            consumerStep.start()

        then:
            1 * consumer.start()
    }

    def "should stop consuming"() {
        when:
            consumerStep.stop()

        then:
            1 * consumer.stop()
    }

    def "should invoke next step"() {
        when:
            consumerStep.handle("message")

        then:
            1 * nextStep.handle("message")
    }
}
