package com.altumpoint.easypipe.core.steps;

/**
 * Interface for EasyPipe message transformers.
 *
 * @param <M> Type of messages.
 * @since 0.1.0
 */
public interface EasyTransformer<M, R> extends StageComponent {

    R transform(M message);
}
