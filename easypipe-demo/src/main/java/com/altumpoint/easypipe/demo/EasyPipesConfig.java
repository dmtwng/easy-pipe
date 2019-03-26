package com.altumpoint.easypipe.demo;

import com.altumpoint.easypipe.core.EasyPipeline;
import com.altumpoint.easypipe.core.PipelineContext;
import com.altumpoint.easypipe.core.pipes.TypedProperties;
import com.altumpoint.easypipe.core.pipes.simple.SimplePipeBuilder;
import com.altumpoint.easypipe.demo.pipes.DoublesConsumer;
import com.altumpoint.easypipe.demo.pipes.LogsPublisher;
import com.altumpoint.easypipe.demo.pipes.PercentsTransformer;
import com.altumpoint.easypipe.fileio.DirectoryListener;
import com.altumpoint.easypipe.fileio.FilePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.altumpoint.easypipe.core")
@ConfigurationProperties(prefix = "easypipe")
public class EasyPipesConfig {

    private TypedProperties auditorConsumer;

    private TypedProperties auditorPublisher;


    @Autowired
    @EasyPipeline(name = "doubles-stream", autostart = false)
    public PipelineContext doublesStream(
            SimplePipeBuilder pipeBuilder,
            DoublesConsumer doublesConsumer,
            PercentsTransformer percentsTransformer,
            LogsPublisher logsPublisher
    ) {
        return pipeBuilder
                .startPipe("doubles-stream")
                .withSource("doubles-consumer", doublesConsumer)
                .transform("d-transformer", percentsTransformer)
                .publish("d-publisher", logsPublisher)
                .build();
    }

    @Autowired
    @EasyPipeline(name = "created-files-auditor", autostart = false)
    public PipelineContext createdFilesAuditor(SimplePipeBuilder pipeBuilder) {
        return pipeBuilder
                .startPipe("created-files-auditor")
                .withSource("files-auditor-wather", new DirectoryListener("temp/watch"), auditorConsumer)
                .publish("files-auditor-writer", new FilePublisher("temp/audit.log"), auditorPublisher)
                .build();
    }


    public void setAuditorConsumer(TypedProperties auditorConsumer) {
        this.auditorConsumer = auditorConsumer;
    }

    public void setAuditorPublisher(TypedProperties auditorPublisher) {
        this.auditorPublisher = auditorPublisher;
    }

}
