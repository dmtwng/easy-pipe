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

    def "pipe definition should store status"() {
        given:
        def pipeInfo = new EasyPipeInfo()

        when:
        pipeInfo.setStatus(EasyPipeInfo.Status.RUNNING)

        then:
        EasyPipeInfo.Status.RUNNING == pipeInfo.getStatus()
    }

}
