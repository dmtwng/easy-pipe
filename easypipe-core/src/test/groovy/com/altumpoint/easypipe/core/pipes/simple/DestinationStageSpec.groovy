package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.meters.MetersStrategy
import com.altumpoint.easypipe.core.pipes.EasyDestination
import spock.lang.Specification

class DestinationStageSpec extends Specification {

    private destination
    private metersStrategy
    private destinationStage

    void setup() {
        destination = Mock(EasyDestination)
        metersStrategy = Mock(MetersStrategy)
        destinationStage = new DestinationStage<String>(destination, metersStrategy)
    }

    def "should publish message"() {
        when:
        destinationStage.handle("message")

        then:
        1 * destination.publish("message")
    }

    def "should publish message and invoke next stage"() {
        given:
        def nextStage = Mock(SimpleStage)
        destinationStage.setNextStage(nextStage)

        when:
        destinationStage.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * this.destination.publish("message")
        1 * metersStrategy.afterHandling(_)
        1 * nextStage.handle("message")
    }
}
