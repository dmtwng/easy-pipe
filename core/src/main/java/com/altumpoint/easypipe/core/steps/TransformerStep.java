package com.altumpoint.easypipe.core.steps;

/**
 * Pipe step for message transformation / translation.
 *
 * @param <M> type of message ty handle.
 * @param <R> type of result of transformation.
 * @since 0.1.0
 */
public class TransformerStep<M, R> implements EasyPipeStep<M> {

    private EasyTransformer<M, R> transformer;

    private EasyPipeStep<R> nextStep;

    public TransformerStep(EasyTransformer<M, R> transformer) {
        this.transformer = transformer;
    }


    @Override
    public void handle(M message) {
        this.nextStep.handle(this.transformer.transform(message));
    }

    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }
}
