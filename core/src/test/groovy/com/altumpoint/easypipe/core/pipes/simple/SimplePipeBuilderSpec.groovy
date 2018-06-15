package com.altumpoint.easypipe.core.pipes.simple

import com.altumpoint.easypipe.core.pipes.EasySource
import com.altumpoint.easypipe.core.pipes.EasyDestination
import com.altumpoint.easypipe.core.pipes.EasyTransformer
import com.altumpoint.easypipe.core.pipes.TypedProperties
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

        consumer = Mock(EasySource)
        def msgConsumer
        consumer.setMessageConsumer(_ as Consumer) >> {Consumer setConsumer -> msgConsumer = setConsumer}
        consumer.start() >> {msgConsumer.accept("test message")}

        transformer = Mock(EasyTransformer)
        transformer.transform("test message") >> "transformed message"

        publisher = Mock(EasyDestination)
    }


    def "should build simple pipeline"() {
        given: "simple pipe builder"
        def pipeBuilder = new SimplePipeBuilder(this.meterRegistry)

        when: "build and start pipeline"
        pipeBuilder
                .startPipe("test-pipe")
                .addSource("test-source", consumer)
                .addTransformer("test-transformer", transformer)
                .addDestination("test-destination", publisher)
                .build()
                .start()

        then: "message from source should be published"
        1 * publisher.publish("transformed message")

    }

    def "should build simple pipeline with component properties"() {
        given: "simple pipe builder"
        def pipeBuilder = new SimplePipeBuilder(meterRegistry)

        and: "stage component properties"
        def properties = new TypedProperties()

        when: "build and start pipeline"
        pipeBuilder
                .startPipe("test-pipe")
                .addSource("test-source", consumer, properties)
                .addTransformer("test-transformer", transformer, properties)
                .addDestination("test-destination", publisher, properties)
                .build()
                .start()

        then: "message from source should be published"
        1 * consumer.loadProperties(properties)
        1 * transformer.loadProperties(properties)
        1 * publisher.loadProperties(properties)
        1 * publisher.publish("transformed message")
    }

    def "should throw exception if add transformer with no consumer"() {
        given:
        def pipeBuilder = new SimplePipeBuilder(Mock(MeterRegistry))

        when:
        pipeBuilder.addTransformer("test-transfomer", Mock(EasyTransformer))

        then:
        thrown IllegalStateException
    }

    def "should throw exception if add publisher with no consumer"() {
        given:
        def pipeBuilder = new SimplePipeBuilder(Mock(MeterRegistry))

        when:
        pipeBuilder.addDestination("test-destination", Mock(EasyDestination))

        then:
        thrown IllegalStateException
    }

    def "should throw exception if add two consumers"() {
        given:
        def pipeBuilder = new SimplePipeBuilder(Mock(MeterRegistry))

        when:
        pipeBuilder
                .addSource("test-consumer1", Mock(EasySource))
                .addSource("test-consumer2", Mock(EasySource))

        then:
        thrown IllegalStateException
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
