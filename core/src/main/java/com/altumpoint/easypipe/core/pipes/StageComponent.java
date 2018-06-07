package com.altumpoint.easypipe.core.pipes;

/**
 * Common interface for stage components.
 * Stage component is an implementation of stage functionality.
 *
 * @since 0.2.0
 */
public interface StageComponent {

    /**
     * Load all needed properties into stage component.
     *
     * @param properties properties to load.
     */
    default void loadProperties(TypedProperties properties) {
        // No implementation needed by default
    }
}
