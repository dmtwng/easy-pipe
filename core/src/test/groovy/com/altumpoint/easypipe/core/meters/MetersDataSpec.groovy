package com.altumpoint.easypipe.core.meters

import spock.lang.Specification

class MetersDataSpec extends Specification {

    def "should store objects"() {
        given:
        def metersData = new MetersData()
        def meterObject = new Object()

        when:
        metersData.addMeterData("KEY", meterObject)

        then:
        meterObject == metersData.getMeterData("KEY")
    }

    def "should count time with stop watch"() {
        given:
        def metersData = new MetersData()

        and:
        def delay = 50
        def delta = delay / 10

        when:
        metersData.addStopWatch("KEY")

        and:
        sleep(delay)

        and:
        def timerValue = metersData.getStopWatchTaskTime("KEY")

        then:
        timerValue < delay + delta
        timerValue > delay - delta
    }
}
