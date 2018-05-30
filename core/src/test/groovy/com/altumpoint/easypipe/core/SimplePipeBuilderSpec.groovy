package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.stages.EasyConsumer
import com.altumpoint.easypipe.core.stages.EasyPublisher
import com.altumpoint.easypipe.core.stages.EasyTransformer
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

        and:
        def consumer = Mock(EasyConsumer)
        def msgConsumer
        consumer.setMessageConsumer(_ as Consumer) >> {Consumer setConsumer -> msgConsumer = setConsumer}
        consumer.start() >> {msgConsumer.accept("test message")}

        and:
        def transformer = Mock(EasyTransformer)
        transformer.transform("test message") >> "transformed message"

        and:
        def publisher = Mock(EasyPublisher)

        when:
        pipeBuilder
                .startPipe("test-pipe", consumer)
                .addTransformer("test-transformer", transformer)
                .addPublisher("test-publisher", publisher)
                .build()
                .start()

        then:
        1 * publisher.publish("transformed message")

    }

    def "should throw exception if no stages added"() {
        given:
        def pipeBuilder = new SimplePipeBuilder(Mock(MeterRegistry))

        when:
        pipeBuilder.build()

        then:
        thrown IllegalStateException
    }
}
