package com.altumpoint.easypipe.core.meters

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong

class DefaultMetersStrategyTest extends Specification {
    private static final String STOP_WATCH_KEY = "start-stopwatch"

    private counter
    private gauge
    private meterRegistry
    private metersStrategy

    void setup() {
        counter = Mock(Counter)
        gauge = Mock(AtomicLong)
        meterRegistry = Mock(MeterRegistry)
        meterRegistry.counter(_) >> counter
        meterRegistry.gauge(_, _) >> gauge
        metersStrategy = new DefaultMetersStrategy("test", meterRegistry)
    }

    def "should increase counter before message handling"() {
        when:
        metersStrategy.beforeHandling()

        then:
        1 * counter.increment()
    }

    def "should start timer before message handling"() {
        when:
        def metersData = metersStrategy.beforeHandling()

        then:
        metersData != null
        metersData.getStopWatchTaskTime(STOP_WATCH_KEY) >= 0
    }

    def "should record time after message handling"() {
        given:
        def metersData = Mock(MetersData)

        when:
        metersStrategy.afterHandling(metersData)

        then:
        1 * metersData.getStopWatchTaskTime(STOP_WATCH_KEY)
    }
}
