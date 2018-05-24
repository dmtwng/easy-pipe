package com.altumpoint.easypipe.core

import spock.lang.Specification

class PipeRunnableSpec extends Specification {

    def "pipe should be started"() {
        given:
            def pipe = Mock(EasyPipe)
            def runnable = new PipeRunnable(pipe)

        when:
            runnable.run()

        then:
            1 * pipe.start()
    }
}
