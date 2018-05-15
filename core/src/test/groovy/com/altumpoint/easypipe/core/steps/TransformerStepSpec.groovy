package com.altumpoint.easypipe.core.steps

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong

class TransformerStepSpec extends Specification {
    private transformer
    private meterRegistry
    private transformerStep
    private nextStep

    void setup() {
        transformer = Mock(EasyTransformer)
        meterRegistry = Mock(SimpleMeterRegistry)
        meterRegistry.counter("easy-pipe.test.count") >> Mock(Counter)
        meterRegistry.gauge("easy-pipe.test.time", _) >> Mock(AtomicLong)
        transformerStep = new TransformerStep<String, String>("test", transformer, meterRegistry)
        nextStep = Mock(EasyPipeStep)
        transformerStep.setNextStep(nextStep)
    }

    def "should transform message and invoke next step"() {
        given:
        transformer.transform("message") >> "result"

        when:
        transformerStep.handle("message")

        then:
        1 * nextStep.handle("result")
    }

}
