package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.stages.EasyConsumer;
import com.altumpoint.easypipe.core.stages.EasyPublisher;
import com.altumpoint.easypipe.core.stages.EasyTransformer;
import com.altumpoint.easypipe.core.stages.TypedProperties;

/**
 * Main interface for EasyPipe builders.
 *
 * @since 0.2.0
 */
public interface EasyPipeBuilder {

    /**
     * Initialize builder with pipe entry point: consumer.
     *
     * @param pipeName name of pipe.
     * @return builder instance.
     */
    SimplePipeBuilder startPipe(String pipeName);

    /**
     * Adds consumer stage into the pipe.
     *
     * @param stageName name of pipe.
     * @param consumer pipe entry point.
     * @return builder instance.
     */
    default  <M> SimplePipeBuilder addConsumer(String stageName, EasyConsumer<M> consumer) {
        return addConsumer(stageName, consumer, null);
    }

    /**
     * Adds consumer stage into the pipe.
     * Loads consumer properties.
     *
     * @param stageName name of pipe.
     * @param consumer pipe entry point.
     * @param properties consumer properties.
     * @return builder instance.
     */
    <M> SimplePipeBuilder addConsumer(String stageName, EasyConsumer<M> consumer, TypedProperties properties);

    /**
     * Adds transformer stage into the pipe.
     *
     * @param stageName name of stage.
     * @param transformer stage component.
     * @return builder instance.
     */
    default <M, R> SimplePipeBuilder addTransformer(String stageName, EasyTransformer<M, R> transformer) {
        return addTransformer(stageName, transformer, null);
    }

    /**
     * Adds transformer stage into the pipe.
     * Loads transformer properties.
     *
     * @param stageName name of stage.
     * @param transformer stage component.
     * @param properties transformer properties.
     * @return builder instance.
     */
    <M, R> SimplePipeBuilder addTransformer(
            String stageName, EasyTransformer<M, R> transformer, TypedProperties properties);

    /**
     * Adds publisher into the pipe.
     *
     * @param stageName name of stage.
     * @param publisher stage component.
     * @return builder instance.
     */
    default  <M> SimplePipeBuilder addPublisher(String stageName, EasyPublisher<M> publisher) {
        return addPublisher(stageName, publisher, null);
    }

    /**
     * Adds publisher into the pipe.
     * Loads publisher properties.
     *
     * @param stageName name of stage.
     * @param publisher stage component.
     * @param properties publisher properties.
     * @return builder instance.
     */
    <M> SimplePipeBuilder addPublisher(String stageName, EasyPublisher<M> publisher, TypedProperties properties);


    /**
     * Builds and returns simple easy pipe.
     *
     * @return instance of easy pipe.
     */
    EasyPipe build();
}
