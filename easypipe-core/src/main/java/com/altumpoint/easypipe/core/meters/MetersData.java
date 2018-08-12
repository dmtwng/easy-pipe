package com.altumpoint.easypipe.core.meters;

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

    public void startTimer(String key) {
        dataMap.put(key, System.nanoTime());
    }

    public Object getMeterData(String key) {
        return dataMap.get(key);
    }

    public long getTimerDuration(String key) {
        return System.nanoTime() - (Long) dataMap.get(key);
    }

}
