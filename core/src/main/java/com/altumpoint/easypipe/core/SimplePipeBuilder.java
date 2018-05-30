package com.altumpoint.easypipe.core;

import com.altumpoint.easypipe.core.meters.DefaultMetersStrategy;
import com.altumpoint.easypipe.core.stages.ConsumerStage;
import com.altumpoint.easypipe.core.stages.EasyConsumer;
import com.altumpoint.easypipe.core.stages.EasyPipeStage;
import com.altumpoint.easypipe.core.stages.EasyPublisher;
import com.altumpoint.easypipe.core.stages.EasyTransformer;
import com.altumpoint.easypipe.core.stages.PublisherStage;
import com.altumpoint.easypipe.core.stages.TransformerStage;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Builder class for EasyPipes instances.
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

    public <M> SimplePipeBuilder startPipe(String pipeName, EasyConsumer<M> consumer) {
        this.pipeName = pipeName;
        ConsumerStage<M> consumerStage = new ConsumerStage<>(
                consumer, new DefaultMetersStrategy(stageFullName("consumer"), meterRegistry));
        stages.add(consumerStage);
        return this;
    }

    public <M, R> SimplePipeBuilder addTransformer(String stageName, EasyTransformer<M, R> transformer) {
        stages.add(new TransformerStage<>(
                transformer, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry)));
        return this;
    }

    public <M> SimplePipeBuilder addPublisher(String stageName, EasyPublisher<M> publisher) {
        PublisherStage<M> publisherStage = new PublisherStage<>(
                publisher, new DefaultMetersStrategy(stageFullName(stageName), meterRegistry));
        stages.add(publisherStage);
        return this;
    }


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
}
