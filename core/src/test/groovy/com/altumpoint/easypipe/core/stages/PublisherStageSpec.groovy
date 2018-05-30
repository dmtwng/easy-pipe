package com.altumpoint.easypipe.core.stages

import com.altumpoint.easypipe.core.meters.MetersStrategy
import spock.lang.Specification

class PublisherStageSpec extends Specification {

    private publisher
    private metersStrategy
    private publisherStage

    void setup() {
        publisher = Mock(EasyPublisher)
        metersStrategy = Mock(MetersStrategy)
        publisherStage = new PublisherStage<String>(publisher, metersStrategy)
    }

    def "should publish message"() {
        when:
        publisherStage.handle("message")

        then:
        1 * publisher.publish("message")
    }

    def "should publish message and invoke next stage"() {
        given:
        def nextStage = Mock(EasyPipeStage)
        publisherStage.setNextStage(nextStage)

        when:
        publisherStage.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * this.publisher.publish("message")
        1 * metersStrategy.afterHandling(_)
        1 * nextStage.handle("message")
    }
}
