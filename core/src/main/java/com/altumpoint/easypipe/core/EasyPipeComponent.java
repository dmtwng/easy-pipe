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

    String value() default "";

}
