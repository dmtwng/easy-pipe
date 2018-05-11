package com.altumpoint.easypipe.core.steps

import spock.lang.Specification

class ConsumerStepTest extends Specification {

    private consumer
    private consumerStep
    private nextStep

    void setup() {
        consumer = Mock(EasyConsumer)
        consumerStep = new ConsumerStep<String>(this.consumer)
        nextStep = Mock(EasyPipeStep)
        this.consumerStep.setNextStep(this.nextStep)
    }

    void cleanup() {
    }


    def "should start consuming"() {
        when:
            this.consumerStep.start()

        then:
            1 * this.consumer.start()
    }

    def "should stop consuming"() {
        when:
            this.consumerStep.stop()

        then:
            1 * this.consumer.stop()
    }

    def "should invoke next step"() {
        when:
            this.consumerStep.handle("message")

        then:
            1 * this.nextStep.handle("message")
    }
}
