package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.stages.EasyConsumer
import com.altumpoint.easypipe.core.stages.EasyPublisher
import com.altumpoint.easypipe.core.stages.EasyTransformer
import com.altumpoint.easypipe.core.stages.TypedProperties
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

class SimplePipeBuilderSpec extends Specification {

    private meterRegistry

    private consumer
    private transformer
    private publisher


    def setup() {
        meterRegistry = Mock(MeterRegistry)
        this.meterRegistry.counter(_ as String) >> Mock(Counter)
        this.meterRegistry.gauge(_ as String, _ as AtomicLong) >> Mock(AtomicLong)

        consumer = Mock(EasyConsumer)
        def msgConsumer
        consumer.setMessageConsumer(_ as Consumer) >> {Consumer setConsumer -> msgConsumer = setConsumer}
        consumer.start() >> {msgConsumer.accept("test message")}

        transformer = Mock(EasyTransformer)
        transformer.transform("test message") >> "transformed message"

        publisher = Mock(EasyPublisher)
    }


    def "should build simple pipeline"() {
        given: "simple pipe builder"
        def pipeBuilder = new SimplePipeBuilder(this.meterRegistry)

        when: "build and start pipeline"
        pipeBuilder
                .startPipe("test-pipe", consumer)
                .addTransformer("test-transformer", transformer)
                .addPublisher("test-publisher", publisher)
                .build()
                .start()

        then: "message from consumer should be published"
        1 * publisher.publish("transformed message")

    }

    def "should build simple pipeline with component properties"() {
        given: "simple pipe builder"
        def pipeBuilder = new SimplePipeBuilder(meterRegistry)

        and: "stage component properties"
        def properties = new TypedProperties()

        when: "build and start pipeline"
        pipeBuilder
                .startPipe("test-pipe", consumer, properties)
                .addTransformer("test-transformer", transformer, properties)
                .addPublisher("test-publisher", publisher, properties)
                .build()
                .start()

        then: "message from consumer should be published"
        1 * consumer.loadProperties(properties)
        1 * transformer.loadProperties(properties)
        1 * publisher.loadProperties(properties)
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
