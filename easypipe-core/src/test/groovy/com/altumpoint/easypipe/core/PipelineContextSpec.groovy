package com.altumpoint.easypipe.core

import com.altumpoint.easypipe.core.pipes.EasyPipe
import spock.lang.Specification

class PipelineContextSpec extends Specification {

    def "pipe context should store pipe"() {
        given:
        def context = new PipelineContext()
        def pipe = Mock(EasyPipe)

        when:
        context.setPipe(pipe)

        then:
        pipe == context.getPipe()
    }

    def "pipe context should store status"() {
        given:
        def context = new PipelineContext()

        when:
        context.setStatus(PipelineContext.Status.RUNNING)

        then:
        PipelineContext.Status.RUNNING == context.getStatus()
    }

}
