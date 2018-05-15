package com.altumpoint.easypipe.core.steps

import spock.lang.Specification

class PublisherStepSpec extends Specification {

    private publisher
    private publisherStep

    void setup() {
        publisher = Mock(EasyPublisher)
        publisherStep = new PublisherStep<String>(publisher)
    }

    def "should publish message"() {
        when:
            publisherStep.handle("message")

        then:
            1 * publisher.publish("message")
    }

    def "should publish message and invoke next step"() {
        given:
            def nextStep = Mock(EasyPipeStep)
            publisherStep.setNextStep(nextStep)

        when:
            publisherStep.handle("message")

        then:
            1 * this.publisher.publish("message")
            1 * nextStep.handle("message")
    }
}
