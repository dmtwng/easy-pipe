package com.altumpoint.easypipe.core.meters;

import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Metadata about initialized meters.
 *
 * @since 0.2.0
 */
public class MetersData {

    private Map<String, Object> dataMap = new HashMap<>();

    public void addMeterData(String key, Object data) {
        dataMap.put(key, data);
    }

    public void addStopWatch(String key) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        dataMap.put(key, stopWatch);
    }

    public Object getMeterData(String key) {
        return dataMap.get(key);
    }

    public long getStopWatchTaskTime(String key) {
        StopWatch stopWatch = (StopWatch) dataMap.get(key);
        stopWatch.stop();
        return stopWatch.getLastTaskTimeMillis();
    }

}
