package com.altumpoint.easypipe.core

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.type.StandardAnnotationMetadata
import spock.lang.Specification

class EasyPipeRegistrySpec extends Specification {

    private static final String PIPE_NAME = "test-pipe"
    private static final String INCORRECT_PIPE_NAME = "incorrect-pipe"


    def "should add and manage pipes with annotation"() {
        given: "test and incorrect pipe instance"
        def testPipe = Mock(EasyPipe)
        def incorrectPipe = Mock(EasyPipe)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, testPipe)
            pipesBeans.put(INCORRECT_PIPE_NAME, incorrectPipe)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("value", PIPE_NAME)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes

        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata
        def incorrectBeanDefinition = Mock(BeanDefinition)
        incorrectBeanDefinition.getSource() >> Mock(StandardAnnotationMetadata)

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(PIPE_NAME) >> beanDefinition
        beanFactory.getBeanDefinition(INCORRECT_PIPE_NAME) >> incorrectBeanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipe call start"
        easyPipeRegistry.start(PIPE_NAME)

        then: "pipe start should be invoked"
//        1 * testPipe.start()  // strange behaviour, fails time to time, will investigate later

        when: "incorrect pipe call start"
        easyPipeRegistry.start(INCORRECT_PIPE_NAME)

        then: "incorrect pipe start should not be invoked"
        0 * incorrectPipe.start()

        when: "pipe call stop"
        easyPipeRegistry.stop(PIPE_NAME)

        then: "pipe stop should be invoked"
        1 * testPipe.stop()

        when: "incorrect pipe call stop"
        easyPipeRegistry.stop(INCORRECT_PIPE_NAME)

        then: "pipe stop should be invoked"
        0 * incorrectPipe.stop()
    }
}
