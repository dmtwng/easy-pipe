package com.altumpoint.easypipe.core.meters;

/**
 * Interface for collecting meters about stages execution.
 *
 * @since 0.2.0
 */
public interface MetersStrategy {

    /**
     * Collects all initial meters.
     * Should be called before message handling.
     *
     * @return initialized meters data.
     */
    MetersData beforeHandling();

    /**
     * Measure stage component meters based on initialized meters.
     * Should be called after message handling.
     *
     * @param metersData initialized meters.
     */
    void afterHandling(MetersData metersData);

}
