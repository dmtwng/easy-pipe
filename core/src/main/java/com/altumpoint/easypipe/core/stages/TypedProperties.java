package com.altumpoint.easypipe.core.stages;

import java.util.Properties;

/**
 * Properties, extended with type specific getters.
 *
 * @since 0.2.0
 */
public class TypedProperties extends Properties {

    public int getInt(String key, int defaultValue) {
        String propertyValue = getProperty(key);
        if (propertyValue == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(propertyValue);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String propertyValue = getProperty(key);
        if (propertyValue == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(propertyValue);
        }
    }
}
