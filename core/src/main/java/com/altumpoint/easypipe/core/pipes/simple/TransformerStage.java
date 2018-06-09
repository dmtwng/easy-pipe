package com.altumpoint.easypipe.core.pipes.simple;

import com.altumpoint.easypipe.core.meters.MetersData;
import com.altumpoint.easypipe.core.meters.MetersStrategy;
import com.altumpoint.easypipe.core.pipes.EasyTransformer;

/**
 * Pipe stage for message transformation / translation.
 *
 * @param <M> type of message ty handle.
 * @param <R> type of result of transformation.
 * @since 0.1.0
 */
public class TransformerStage<M, R> extends SimpleStage<M> {

    private EasyTransformer<M, R> transformer;


    public TransformerStage(EasyTransformer<M, R> transformer, MetersStrategy metersStrategy) {
        super(metersStrategy);

        this.transformer = transformer;
    }


    @Override
    public void handle(M message) {
        MetersData metersData = metersStrategy.beforeHandling();
        R transformationResult = transformer.transform(message);
        metersStrategy.afterHandling(metersData);
        nextStage.handle(transformationResult);
    }

}
