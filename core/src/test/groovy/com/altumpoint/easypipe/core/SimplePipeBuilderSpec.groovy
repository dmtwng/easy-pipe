package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.steps.EasyConsumer
import com.altumpoint.easypipe.core.steps.EasyPublisher
import com.altumpoint.easypipe.core.steps.EasyTransformer
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

class SimplePipeBuilderSpec extends Specification {

    def "should build simple pipeline"() {
        given:
        def meterRegistry = Mock(MeterRegistry)
        meterRegistry.counter(_ as String) >> Mock(Counter)
        meterRegistry.gauge(_ as String, _ as AtomicLong) >> Mock(AtomicLong)

        and:
        def pipeBuilder = new SimplePipeBuilder(meterRegistry)
        def consumer = Mock(EasyConsumer)
        def msgConsumer
        consumer.setMessageConsumer(_ as Consumer) >> {Consumer setConsumer -> msgConsumer = setConsumer}
        consumer.start() >> {msgConsumer.accept("test message")}
        def transformer = Mock(EasyTransformer)
        transformer.transform("test message") >> "transformed message"
        def publisher = Mock(EasyPublisher)

        when:
        pipeBuilder
                .startPipe("test-pipe", consumer)
                .addTransformer("test-transformer", transformer)
                .addPublisher(publisher)
                .build()
                .start()

        then:
        1 * publisher.publish("transformed message")

    }

    def "should throw exception if no steps added"() {
        given:
        def pipeBuilder = new SimplePipeBuilder(Mock(MeterRegistry))

        when:
        pipeBuilder.build()

        then:
        thrown IllegalStateException
    }
}
