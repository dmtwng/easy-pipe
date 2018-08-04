package com.altumpoint.easypipe.core;

import org.springframework.context.annotation.Bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for marking EasyPipe instances for component scanning.
 *
 * @since 0.1.0
 */
@Bean
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyPipeComponent {

    /**
     * Name of pipeline. If doesn't specified, name of bean will be used.
     *
     * @return name of pipeline.
     */
    String name() default "";

    /**
     * Do pipeline should be started during context startup.
     * {@code true} by default.
     *
     * @return {@code true} if pipeline should be started on startup, {@code false} otherwise.
     */
    boolean autostart() default true;
}
