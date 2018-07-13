package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.meters.MetersStrategy
import com.altumpoint.easypipe.core.pipes.EasyFilter
import spock.lang.Specification

class FilterStageSpec extends Specification {

    private filter
    private metersStrategy
    private filterStage
    private nextStage

    void setup() {
        filter = Mock(EasyFilter)
        metersStrategy = Mock(MetersStrategy)
        filterStage = new FilterStage<String>(filter, metersStrategy)
        nextStage = Mock(SimpleStage)
        filterStage.setNextStage(nextStage)
    }

    def "should filter out all messages which are not passes"() {
        given: "filter which don't like messages with BAD word"
        filter.passes(_) >> {String message -> !message.contains("BAD")}

        when: "pass three messages"
        filterStage.handle("good message")
        filterStage.handle("very BAD message")
        filterStage.handle("one more good message")

        then: "next stage should handle just good messages"
        1 * nextStage.handle("good message")
        1 * nextStage.handle("one more good message")
        0 * nextStage.handle("very BAD message")
    }
}
