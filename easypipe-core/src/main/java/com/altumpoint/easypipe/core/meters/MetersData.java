package com.altumpoint.easypipe.core.meters;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of meters information before step, to completer meter calculation
 * after step is done.
 *
 * @since 0.2.0
 * @see MetersStrategy
 */
public class MetersData {

    private Map<String, Object> dataMap = new HashMap<>();

    /**
     * Add object with specified key to collection.
     *
     * @param key key of meters object.
     * @param data meters object.
     */
    public void addMeterData(String key, Object data) {
        dataMap.put(key, data);
    }

    /**
     * Starts timer with specified key and adds it to collection.
     * Counting in {@code NANOSECONDS}.
     *
     * @param key key of started timer.
     */
    public void startTimer(String key) {
        dataMap.put(key, System.nanoTime());
    }

    /**
     * Gets meters object from collection.
     *
     * @param key key of meters object.
     * @return meters object.
     */
    public Object getMeterData(String key) {
        return dataMap.get(key);
    }

    /**
     * Gets timer duration in {@code NANOSECONDS}.
     * @param key key of timer.
     * @return duration in {@code NANOSECONDS}.
     */
    public long getTimerDuration(String key) {
        return System.nanoTime() - (Long) dataMap.get(key);
    }

}
