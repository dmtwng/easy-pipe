package com.altumpoint.easypipe.core.meters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Meters strategy with two main meters: messages count and stage component time.
 *
 * @since 0.2.0
 */
public class DefaultMetersStrategy implements MetersStrategy {

    private static final String KEY_START_STOPWATCH = "start-stopwatch";

    private final Counter counter;
    private final AtomicLong timeGauge;


    public DefaultMetersStrategy(String metersName, MeterRegistry meterRegistry) {
        counter = meterRegistry.counter(String.format("easy-pipe.%s.count", metersName));
        timeGauge = meterRegistry.gauge(String.format("easy-pipe.%s.time", metersName), new AtomicLong());
    }


    @Override
    public MetersData beforeHandling() {
        counter.increment();

        MetersData metersData = new MetersData();
        metersData.addStopWatch(KEY_START_STOPWATCH);
        return metersData;
    }

    @Override
    public void afterHandling(MetersData metersData) {
        timeGauge.set(metersData.getStopWatchTaskTime(KEY_START_STOPWATCH));
    }
}
