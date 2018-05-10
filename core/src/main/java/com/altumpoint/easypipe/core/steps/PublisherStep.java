package com.altumpoint.easypipe.core.steps;

/**
 * Pipe step for message publishing.
 *
 * It also could transfer message to next step, if it defined.
 *
 * @param <M> type of message.
 * @since 0.1.0
 */
public class PublisherStep<M> implements PipeStep<M> {

    private EasyPublisher<M> publisher;

    private PipeStep<M> nextStep;

    @Override
    public void handle(M message) {
        this.publisher.publish(message);

        if (this.nextStep != null) {
            this.nextStep.handle(message);
        }
    }

    public void setPublisher(EasyPublisher<M> publisher) {
        this.publisher = publisher;
    }

    public void setNextStep(PipeStep nextStep) {
        this.nextStep = nextStep;
    }
}
