package com.altumpoint.easypipe.core.meters

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import spock.lang.Specification

class DefaultMetersStrategyTest extends Specification {
    private static final String STOP_WATCH_KEY = "start-stopwatch"

    private counter
    private timer
    private meterRegistry
    private metersStrategy

    void setup() {
        counter = Mock(Counter)
        timer = Mock(Timer)
        meterRegistry = Mock(MeterRegistry)
        meterRegistry.counter("easy-pipe.test.count") >> counter
        meterRegistry.timer("easy-pipe.test.time") >> timer
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
        metersData.getTimerDuration(STOP_WATCH_KEY) >= 0
    }

    def "should record time after message handling"() {
        given:
        def metersData = Mock(MetersData)

        when:
        metersStrategy.afterHandling(metersData)

        then:
        1 * metersData.getTimerDuration(STOP_WATCH_KEY)
    }
}
