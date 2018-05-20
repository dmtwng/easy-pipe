package com.altumpoint.easypipe.core.steps

import com.altumpoint.easypipe.core.meters.MetersStrategy
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import spock.lang.Specification


class TransformerStepSpec extends Specification {

    private transformer
    private metersStrategy
    private transformerStep
    private nextStep

    void setup() {
        transformer = Mock(EasyTransformer)
        metersStrategy = Mock(MetersStrategy)
        transformerStep = new TransformerStep<String, String>(transformer, metersStrategy)
        nextStep = Mock(EasyPipeStep)
        transformerStep.setNextStep(nextStep)
    }

    def "should transform message and invoke next step"() {
        given:
        transformer.transform("message") >> "result"

        when:
        transformerStep.handle("message")

        then:
        1 * metersStrategy.beforeHandling()
        1 * metersStrategy.afterHandling(_)
        1 * nextStep.handle("result")
    }

}
