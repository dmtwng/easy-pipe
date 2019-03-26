package com.altumpoint.easypipe.core.meters;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

/**
 * Meters strategy with two main meters: messages count and stage component time.
 *
 * @since 0.2.0
 */
public class DefaultMetersStrategy implements MetersStrategy {

    private static final String KEY_START_STOPWATCH = "start-stopwatch";

    private final Counter counter;
    private final Timer timer;


    public DefaultMetersStrategy(String metersName, MeterRegistry meterRegistry) {
        counter = meterRegistry.counter(String.format("easy-pipe.%s.count", metersName));
        timer = meterRegistry.timer(String.format("easy-pipe.%s.time", metersName));
    }


    @Override
    public MetersData beforeHandling() {
        counter.increment();

        MetersData metersData = new MetersData();
        metersData.startTimer(KEY_START_STOPWATCH);
        return metersData;
    }

    @Override
    public void afterHandling(MetersData metersData) {
        timer.record(metersData.getTimerDuration(KEY_START_STOPWATCH), TimeUnit.NANOSECONDS);
    }
}
