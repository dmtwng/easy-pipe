package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.meters.MetersStrategy
import com.altumpoint.easypipe.core.pipes.EasySource
import spock.lang.Specification

class SourceStageSpec extends Specification {

    private source
    private metersStrategy
    private sourceStage
    private nextStage

    void setup() {
        source = Mock(EasySource)
        metersStrategy = Mock(MetersStrategy)
        sourceStage = new SourceStage<String>(source, metersStrategy)
        nextStage = Mock(SimpleStage)
        sourceStage.setNextStage(nextStage)
    }

    def "should start consuming"() {
        when:
        sourceStage.start()

        then:
        1 * source.start()
    }

    def "should stop consuming"() {
        when:
        sourceStage.stop()

        then:
        1 * source.stop()
    }

    def "should invoke next stage"() {
        when:
        sourceStage.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * nextStage.handle("message")
        1 * metersStrategy.afterHandling(_)
    }
}
