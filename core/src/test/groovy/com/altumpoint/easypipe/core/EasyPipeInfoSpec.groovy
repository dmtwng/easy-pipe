package com.altumpoint.easypipe.core

import spock.lang.Specification

class EasyPipeInfoSpec extends Specification {

    def "pipe definition should store pipe"() {
        given:
        def pipeInfo = new EasyPipeInfo()
        def pipe = Mock(EasyPipe)

        when:
        pipeInfo.setPipe(pipe)

        then:
        pipe == pipeInfo.getPipe()
    }

    def "pipe definition should store thread"() {
        given:
        def pipeInfo = new EasyPipeInfo()
        def thread = Mock(Thread)

        when:
        pipeInfo.setThread(thread)

        then:
        thread == pipeInfo.getThread()
    }

    def "pipe definition should store pipe runnable"() {
        given:
        def pipeInfo = new EasyPipeInfo()
        def runnable = Mock(PipeRunnable)

        when:
        pipeInfo.setRunnable(runnable)

        then:
        runnable == pipeInfo.getRunnable()
    }
}
