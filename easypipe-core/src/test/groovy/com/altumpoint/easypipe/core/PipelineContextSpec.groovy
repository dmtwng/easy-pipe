package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.pipes.EasyPipe
import org.glassfish.hk2.runlevel.RunLevelException
import spock.lang.Specification

class PipelineContextSpec extends Specification {

    public static final String PIPE_NAME = "test-name"

    private pipeline
    private context


    void setup() {
        pipeline = Mock(EasyPipe)
        context = new PipelineContext()
        this.context.setPipeName(PIPE_NAME)
        this.context.setPipe(this.pipeline)
    }


    def "pipeline should be started in separate thread"() {
        when: "pipeline is starting"
        context.start()

        then: "pipe should be started and status changed"
        context.status == PipelineContext.Status.RUNNING
    }

    def "correct status should be set if pipeline start will fail"() {
        given: "broken pipeline"
        this.pipeline.start() >> {
            throw new RuntimeException()
        }

        when: "pipeline is starting"
        context.start()

        and: "wait a bit"
        sleep 500

        then: "pipe should be started and status changed"
        context.status == PipelineContext.Status.FAILED
    }

    def "pipeline should be stopped correctly"() {
        when: "pipeline is stopping"
        def result = context.stop()

        then: "pipe should be stopped and status changed"
        result == true
        context.status == PipelineContext.Status.PENDING
    }

    def "correct status should be set if pipeline stop will fail"() {
        given: "broken pipeline"
        this.pipeline.stop() >> {
            throw new RuntimeException()
        }

        when: "pipeline is starting"
        def result = context.stop()

        then: "pipe should be started and status changed"
        result == false
        context.status == PipelineContext.Status.FAILED
    }

    def "pipeline context should store pipe"() {
        given: "context and pipeline"
        def context = new PipelineContext()
        def pipe = Mock(EasyPipe)

        when: "pipeline is set into context"
        context.setPipe(pipe)

        then: "pipeline should be available"
        pipe == context.getPipe()
    }

    def "pipeline context should store name"() {
        given: "pipeline context"
        def context = new PipelineContext()

        when: "name is set"
        context.setPipeName(PIPE_NAME)

        then: "name should be the same"
        context.getPipeName() == PIPE_NAME
    }

    def "pipeline context should have pending status by default"() {
        given: "pipeline context"
        def context = new PipelineContext()

        expect: "status should be PENDING"
        context.status == PipelineContext.Status.PENDING
    }

}
