package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.meters.MetersStrategy
import com.altumpoint.easypipe.core.pipes.EasyConsumer
import com.altumpoint.easypipe.core.pipes.EasyPipeStage
import com.altumpoint.easypipe.core.pipes.simple.ConsumerStage
import spock.lang.Specification

class ConsumerStageSpec extends Specification {

    private consumer
    private metersStrategy
    private consumerStage
    private nextStage

    void setup() {
        consumer = Mock(EasyConsumer)
        metersStrategy = Mock(MetersStrategy)
        consumerStage = new ConsumerStage<String>(consumer, metersStrategy)
        nextStage = Mock(EasyPipeStage)
        consumerStage.setNextStage(nextStage)
    }

    def "should start consuming"() {
        when:
        consumerStage.start()

        then:
        1 * consumer.start()
    }

    def "should stop consuming"() {
        when:
        consumerStage.stop()

        then:
        1 * consumer.stop()
    }

    def "should invoke next stage"() {
        when:
        consumerStage.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * nextStage.handle("message")
        1 * metersStrategy.afterHandling(_)
    }
}
