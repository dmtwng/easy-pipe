package com.altumpoint.easypipe.core.steps

import com.altumpoint.easypipe.core.meters.MetersStrategy
import spock.lang.Specification

class PublisherStepSpec extends Specification {

    private publisher
    private metersStrategy
    private publisherStep

    void setup() {
        publisher = Mock(EasyPublisher)
        metersStrategy = Mock(MetersStrategy)
        publisherStep = new PublisherStep<String>(publisher, metersStrategy)
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
        1 * metersStrategy.beforeHandling()
        1 * this.publisher.publish("message")
        1 * metersStrategy.afterHandling(_)
        1 * nextStep.handle("message")
    }
}
