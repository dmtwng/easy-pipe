package com.altumpoint.easypipe.core.steps;

/**
 * Pipe step for message publishing.
 *
 * It also could transfer message to next step, if it defined.
 *
 * @param <M> type of message.
 * @since 0.1.0
 */
public class PublisherStep<M> implements EasyPipeStep<M> {

    private EasyPublisher<M> publisher;

    private EasyPipeStep<M> nextStep;

    public PublisherStep(EasyPublisher<M> publisher) {
        this.publisher = publisher;
    }


    @Override
    public void handle(M message) {
        publisher.publish(message);

        if (nextStep != null) {
            nextStep.handle(message);
        }
    }

    @Override
    public void setNextStep(EasyPipeStep nextStep) {
        this.nextStep = nextStep;
    }
}
