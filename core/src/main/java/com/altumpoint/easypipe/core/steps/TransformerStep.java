package com.altumpoint.easypipe.core.steps;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Pipe step for message transformation / translation.
 *
 * @param <M> type of message ty handle.
 * @param <R> type of result of transformation.
 * @since 0.1.0
 */
public class TransformerStep<M, R> implements EasyPipeStep<M> {

    private Counter counter;
    private AtomicLong timeGauge;

    private EasyTransformer<M, R> transformer;

    private EasyPipeStep<R> nextStep;

    public TransformerStep(String name, EasyTransformer<M, R> transformer, MeterRegistry meterRegistry) {
        this.transformer = transformer;

        this.counter = meterRegistry.counter(String.format("easy-pipe.%s.count", name));
        this.timeGauge = meterRegistry.gauge(String.format("easy-pipe.%s.time", name), new AtomicLong());
    }


    @Override
    public void handle(M message) {
        counter.increment();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        R transformationResult = this.transformer.transform(message);
        stopWatch.stop();
        timeGauge.set(stopWatch.getLastTaskTimeMillis());

        nextStep.handle(transformationResult);
    }

    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }
}
