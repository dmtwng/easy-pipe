package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.meters.DefaultMetersStrategy;
import com.altumpoint.easypipe.core.stages.ConsumerStage;
import com.altumpoint.easypipe.core.stages.EasyConsumer;
import com.altumpoint.easypipe.core.stages.EasyPipeStage;
import com.altumpoint.easypipe.core.stages.EasyPublisher;
import com.altumpoint.easypipe.core.stages.EasyTransformer;
import com.altumpoint.easypipe.core.stages.PublisherStage;
import com.altumpoint.easypipe.core.stages.StageComponent;
import com.altumpoint.easypipe.core.stages.TransformerStage;
import com.altumpoint.easypipe.core.stages.TypedProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Builder class for Simple EasyPipes instances.
 *
 * @since 0.1.0
 */
@Component
@Scope("prototype")
public final class SimplePipeBuilder {

    private MeterRegistry meterRegistry;

    private String pipeName;

    private Deque<EasyPipeStage> stages;

    @Autowired
    private SimplePipeBuilder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.stages = new ArrayDeque<>();
    }


    /**
     * Initialize builder with pipe entry point: consumer.
     *
     * @param pipeName name of pipe.
     * @param consumer pipe entry point.
     * @param <M> type of consuming messages.
     * @return builder instance.
     */
    public <M> SimplePipeBuilder startPipe(String pipeName, EasyConsumer<M> consumer) {
        return startPipe(pipeName, consumer, null);
    }

    /**
     * Initialize builder with pipe entry point: consumer.
     * Loads consumer properties.
     *
     * @param pipeName name of pipe.
     * @param consumer pipe entry point.
     * @param properties consumer properties.
     * @return builder instance.
     */
    public <M> SimplePipeBuilder startPipe(String pipeName, EasyConsumer<M> consumer, TypedProperties properties) {
        this.pipeName = pipeName;
        loadPropertiesIntoStageComponent(consumer, properties);
        ConsumerStage<M> consumerStage = new ConsumerStage<>(
                consumer, new DefaultMetersStrategy(stageFullName("consumer"), meterRegistry));
        stages.add(consumerStage);
        return this;
    }

    /**
     * Adds transformer stage into the pipe.
     *
     * @param stageName name of stage.
     * @param transformer stage component.
     * @return builder instance.
     */
    public <M, R> SimplePipeBuilder addTransformer(String stageName, EasyTransformer<M, R> transformer) {
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
    public <M, R> SimplePipeBuilder addTransformer(
            String stageName, EasyTransformer<M, R> transformer, TypedProperties properties) {
        loadPropertiesIntoStageComponent(transformer, properties);
        stages.add(new TransformerStage<>(
                transformer, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry)));
        return this;
    }

    /**
     * Adds publisher into the pipe.
     *
     * @param stageName name of stage.
     * @param publisher stage component.
     * @return builder instance.
     */
    public <M> SimplePipeBuilder addPublisher(String stageName, EasyPublisher<M> publisher) {
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
    public <M> SimplePipeBuilder addPublisher(
            String stageName, EasyPublisher<M> publisher, TypedProperties properties) {
        loadPropertiesIntoStageComponent(publisher, properties);
        PublisherStage<M> publisherStage = new PublisherStage<>(
                publisher, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry));
        stages.add(publisherStage);
        return this;
    }


    /**
     * Builds and returns simple easy pipe.
     *
     * @return instance of easy pipe.
     */
    public EasyPipe build() {
        if (this.stages.isEmpty()) {
            throw new IllegalStateException("Cannot build pipe with no stages.");
        }
        Iterator<EasyPipeStage> iterator = this.stages.descendingIterator();
        EasyPipeStage prevStage = iterator.next();
        while (iterator.hasNext()) {
            EasyPipeStage currentStage = iterator.next();
            currentStage.setNextStage(prevStage);
            prevStage = currentStage;
        }

        return new SimplePipe((ConsumerStage) prevStage);
    }

    private String stageFullName(String stageName) {
        return pipeName + "." + stageName;
    }

    private void loadPropertiesIntoStageComponent(StageComponent stageComponent, TypedProperties properties) {
        if (properties != null) {
            stageComponent.loadProperties(properties);
        }
    }
}
