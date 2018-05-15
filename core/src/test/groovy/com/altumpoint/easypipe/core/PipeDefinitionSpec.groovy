package com.altumpoint.easypipe.core

import spock.lang.Specification

class PipeDefinitionSpec extends Specification {

    def "pipe definition should store pipe"() {
        given:
        def pipeDefinition = new PipeDefinition()
        def pipe = Mock(EasyPipe)

        when:
        pipeDefinition.setPipe(pipe)

        then:
        pipe == pipeDefinition.getPipe()
    }

    def "pipe definition should store thread"() {
        given:
        def pipeDefinition = new PipeDefinition()
        def thread = Mock(Thread)

        when:
        pipeDefinition.setThread(thread)

        then:
        thread == pipeDefinition.getThread()
    }

    def "pipe definition should store pipe runnable"() {
        given:
        def pipeDefinition = new PipeDefinition()
        def runnable = Mock(PipeRunnable)

        when:
        pipeDefinition.setRunnable(runnable)

        then:
        runnable == pipeDefinition.getRunnable()
    }
}
