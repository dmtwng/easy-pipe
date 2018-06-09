package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.meters.MetersStrategy
import com.altumpoint.easypipe.core.pipes.EasyTransformer
import spock.lang.Specification

class TransformerStageSpec extends Specification {

    private transformer
    private metersStrategy
    private transformerStage
    private nextStage

    void setup() {
        transformer = Mock(EasyTransformer)
        metersStrategy = Mock(MetersStrategy)
        transformerStage = new TransformerStage<String, String>(transformer, metersStrategy)
        nextStage = Mock(SimpleStage)
        transformerStage.setNextStage(nextStage)
    }

    def "should transform message and invoke next stage"() {
        given:
        transformer.transform("message") >> "result"

        when:
        transformerStage.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * metersStrategy.afterHandling(_)
        1 * nextStage.handle("result")
    }

}
