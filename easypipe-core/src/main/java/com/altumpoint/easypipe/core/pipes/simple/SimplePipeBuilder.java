package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.EasyPipeBuilder;
import com.altumpoint.easypipe.core.meters.DefaultMetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyFilter;
import com.altumpoint.easypipe.core.pipes.EasySource;
import com.altumpoint.easypipe.core.pipes.EasyPipe;
import com.altumpoint.easypipe.core.pipes.EasyDestination;
import com.altumpoint.easypipe.core.pipes.EasyTransformer;
import com.altumpoint.easypipe.core.pipes.StageComponent;
import com.altumpoint.easypipe.core.pipes.TypedProperties;
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
public final class SimplePipeBuilder implements EasyPipeBuilder {

    private MeterRegistry meterRegistry;

    private String pipeName;

    private Deque<SimpleStage> stages;

    @Autowired
    private SimplePipeBuilder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.stages = new ArrayDeque<>();
    }


    @Override
    public SimplePipeBuilder startPipe(String pipeName) {
        this.pipeName = pipeName;
        return this;
    }

    @Override
    public <M> SimplePipeBuilder withSource(String stageName, EasySource<M> source, TypedProperties properties) {
        if (!stages.isEmpty()) {
            throw new IllegalStateException("SimplePipe could contain just one source.");
        }

        loadPropertiesIntoStageComponent(source, properties);
        SourceStage<M> sourceStage = new SourceStage<>(
                source, new DefaultMetersStrategy(stageFullName("source"), meterRegistry));
        stages.add(sourceStage);
        return this;
    }

    @Override
    public <M, R> SimplePipeBuilder transform(
            String stageName, EasyTransformer<M, R> transformer, TypedProperties properties) {
        if (stages.isEmpty()) {
            throw new IllegalStateException("Source should be added first in SimplePipe.");
        }

        loadPropertiesIntoStageComponent(transformer, properties);
        stages.add(new TransformerStage<>(
                transformer, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry)));
        return this;
    }

    @Override
    public <M> EasyPipeBuilder filter(String stageName, EasyFilter<M> filter, TypedProperties properties) {
        if (stages.isEmpty()) {
            throw new IllegalStateException("Source should be added first in SimplePipe.");
        }

        loadPropertiesIntoStageComponent(filter, properties);
        stages.add(new FilterStage<>(filter, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry)));
        return this;
    }

    @Override
    public <M> SimplePipeBuilder publish(
            String stageName, EasyDestination<M> destination, TypedProperties properties) {
        if (stages.isEmpty()) {
            throw new IllegalStateException("Source should be added first in SimplePipe.");
        }

        loadPropertiesIntoStageComponent(destination, properties);
        DestinationStage<M> destinationStage = new DestinationStage<>(
                destination, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry));
        stages.add(destinationStage);
        return this;
    }


    @Override
    public EasyPipe build() {
        if (this.stages.isEmpty()) {
            throw new IllegalStateException("Cannot build pipe with no pipes.");
        }
        Iterator<SimpleStage> iterator = this.stages.descendingIterator();
        SimpleStage prevStage = iterator.next();
        while (iterator.hasNext()) {
            SimpleStage currentStage = iterator.next();
            currentStage.setNextStage(prevStage);
            prevStage = currentStage;
        }

        return new SimplePipe((SourceStage) prevStage);
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
