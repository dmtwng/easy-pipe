package com.altumpoint.easypipe.demo;

import com.altumpoint.easypipe.core.EasyPipe;
import com.altumpoint.easypipe.core.EasyPipeComponent;
import com.altumpoint.easypipe.core.PipeBuilder;
import com.altumpoint.easypipe.demo.pipes.DoublesConsumer;
import com.altumpoint.easypipe.demo.pipes.LogsPublisher;
import com.altumpoint.easypipe.demo.pipes.PercentsTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.altumpoint.easypipe.core")
public class EasyPipesConfig {

    @Autowired
    @EasyPipeComponent("doubles-stream")
    public EasyPipe doublesStream(
            DoublesConsumer doublesConsumer,
            PercentsTransformer percentsTransformer,
            LogsPublisher logsPublisher
    ) {
        return PipeBuilder
                .startPipe(doublesConsumer)
                .addTransformer(percentsTransformer)
                .addPublisher(logsPublisher)
                .build();
    }
}
