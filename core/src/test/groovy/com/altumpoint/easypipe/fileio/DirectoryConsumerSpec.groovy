package com.altumpoint.easypipe.fileio

import spock.lang.Specification

import java.nio.file.Path

class DirectoryConsumerSpec extends Specification {

    def "Start"() {
        given:
        def path = Mock(Path)
        def file = Mock(File)
        file.toPath() >> path
        def anyFile = GroovySpy(File, global: true, useObjenesis: true)
        anyFile.toPath() >> path
        def directoryConsumer = new DirectoryConsumer("/home/wi/prv/dev/github/temp/watch")
//        def thread

        when:
        directoryConsumer.start()

        then:
        1 * new File("/home/wi/prv/dev/github/temp/watch") >> file
        true
    }

    def 'file spy example' () {
        given:
        def mockFile = Mock(File)
        GroovySpy(File, global: true, useObjenesis: true)
        when:
        new DirectoryConsumer("asd").delete()
//        def file = new File('testdir', 'testfile')
//        file.delete()
        then :
        1 * new File(_ as String, _ as String) >> { mockFile }
        1 * mockFile.delete()
    }
}
