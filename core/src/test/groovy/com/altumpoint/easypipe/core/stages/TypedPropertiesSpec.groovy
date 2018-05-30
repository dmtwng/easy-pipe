package com.altumpoint.easypipe.core.stages

import spock.lang.Specification

class TypedPropertiesSpec extends Specification {

    def "should return int property if exists"() {
        given: "typed properties with int property"
        def properties = new TypedProperties()
        properties.setProperty("testProperty", "11")

        expect: "return value of property"
        properties.getInt("testProperty", 10) == 11
    }

    def "should return int default value if not exists"() {
        given: "typed properties with int property"
        def properties = new TypedProperties()

        expect: "return default value"
        properties.getInt("testProperty", 10) == 10
        properties.getInt("testProperty", 20) == 20
    }

    def "should return boolean property if exists"() {
        given: "typed properties with int property"
        def properties = new TypedProperties()
        properties.setProperty("testProperty", "true")

        expect: "return value of property"
        properties.getBoolean("testProperty", false)
    }

    def "should return boolean default value if not exists"() {
        given: "typed properties with int property"
        def properties = new TypedProperties()

        expect: "return default value"
        properties.getBoolean("testProperty", true)
        !properties.getBoolean("testProperty", false)
    }

}
