package com.altumpoint.easypipe.demo;

import com.altumpoint.easypipe.core.EasyPipe;
import com.altumpoint.easypipe.core.EasyPipeComponent;
import com.altumpoint.easypipe.core.SimplePipeBuilder;
import com.altumpoint.easypipe.core.steps.TypedProperties;
import com.altumpoint.easypipe.demo.pipes.DoublesConsumer;
import com.altumpoint.easypipe.demo.pipes.LogsPublisher;
import com.altumpoint.easypipe.demo.pipes.PercentsTransformer;
import com.altumpoint.easypipe.fileio.DirectoryConsumer;
import com.altumpoint.easypipe.fileio.FileEasyPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
@ComponentScan("com.altumpoint.easypipe.core")
@ConfigurationProperties(prefix = "easypipe")
public class EasyPipesConfig {

    private TypedProperties auditorConsumer;

    private TypedProperties auditorPublisher;


    @Autowired
    @EasyPipeComponent("doubles-stream")
    public EasyPipe doublesStream(
            SimplePipeBuilder pipeBuilder,
            DoublesConsumer doublesConsumer,
            PercentsTransformer percentsTransformer,
            LogsPublisher logsPublisher
    ) {
        return pipeBuilder
                .startPipe("doubles-stream", doublesConsumer)
                .addTransformer("d-transformer", percentsTransformer)
                .addPublisher("d-publisher", logsPublisher)
                .build();
    }

    @Autowired
    @EasyPipeComponent("created-files-auditor")
    public EasyPipe createdFilesAuditor(SimplePipeBuilder pipeBuilder) {
        return pipeBuilder
                .startPipe("created-files-auditor", new DirectoryConsumer("temp/watch"))
                .addPublisher("auditor", new FileEasyPublisher("temp/audit.log"))
                .build();
    }


    public void setAuditorConsumer(TypedProperties auditorConsumer) {
        this.auditorConsumer = auditorConsumer;
    }

    public void setAuditorPublisher(TypedProperties auditorPublisher) {
        this.auditorPublisher = auditorPublisher;
    }

}
