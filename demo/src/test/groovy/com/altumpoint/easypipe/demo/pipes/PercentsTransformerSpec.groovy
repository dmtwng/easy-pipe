package com.altumpoint.easypipe.demo.pipes

import spock.lang.Specification

class PercentsTransformerSpec extends Specification {

    def "transform messages"(String message, String result) {
        given:
        def transformer = new PercentsTransformer()

        expect:
        transformer.transform(message) == result

        where:
        message               || result
        "0.9004410096120342"  || "90 %"
        "0.5916095315116109"  || "59 %"
        "0.16272642283812166" || "16 %"
    }

}
