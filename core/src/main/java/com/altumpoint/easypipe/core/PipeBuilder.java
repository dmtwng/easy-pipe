package com.altumpoint.easypipe.core;


import com.altumpoint.easypipe.core.steps.ConsumerStep;
import com.altumpoint.easypipe.core.steps.EasyConsumer;
import com.altumpoint.easypipe.core.steps.EasyPublisher;
import com.altumpoint.easypipe.core.steps.EasyTransformer;
import com.altumpoint.easypipe.core.steps.EasyPipeStep;
import com.altumpoint.easypipe.core.steps.PublisherStep;
import com.altumpoint.easypipe.core.steps.TransformerStep;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Builder class for EasyPipes instances.
 *
 * @since 0.1.0
 */
public final class PipeBuilder {

    private Deque<EasyPipeStep> steps;

    private PipeBuilder() {
        this.steps = new ArrayDeque<>();
    }

    public static <M> PipeBuilder startPipe(EasyConsumer<M> consumer) {
        PipeBuilder builder = new PipeBuilder();
        ConsumerStep<M> consumerStep = new ConsumerStep<>(consumer);
        builder.steps.add(consumerStep);
        return builder;
    }

    public <M, R> PipeBuilder addTransformer(EasyTransformer<M, R> transformer) {
        TransformerStep<M, R> transformerStep = new TransformerStep<>(transformer);
        steps.add(transformerStep);
        return this;
    }

    public <M> PipeBuilder addPublisher(EasyPublisher<M> publisher) {
        PublisherStep<M> publisherStep = new PublisherStep<>(publisher);
        steps.add(publisherStep);
        return this;
    }


    public EasyPipe build() {
        if (this.steps.size() < 1) {
            throw new IllegalStateException("Cannot build pipe with no steps.");
        }
        Iterator<EasyPipeStep> iterator = this.steps.descendingIterator();
        EasyPipeStep prevStep = iterator.next();
        while (iterator.hasNext()) {
            EasyPipeStep currentStep = iterator.next();
            currentStep.setNextStep(prevStep);
            prevStep = currentStep;
        }

        return (EasyPipe) prevStep;
    }
}
