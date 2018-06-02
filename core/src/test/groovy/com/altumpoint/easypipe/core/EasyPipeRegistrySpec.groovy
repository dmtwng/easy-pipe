package com.altumpoint.easypipe.core

import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.type.StandardAnnotationMetadata
import spock.lang.Specification

class EasyPipeRegistrySpec extends Specification {

    private static final String PIPE_NAME = "test-pipe"
    private static final String INCORRECT_PIPE_NAME = "incorrect-pipe"


    def "should add and manage correct pipes with annotation"() {
        given: "pipe instance"
        def testPipe = Mock(EasyPipe)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, testPipe)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("value", PIPE_NAME)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes
        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(PIPE_NAME) >> beanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipe call start"
        def startResult = easyPipeRegistry.start(PIPE_NAME)

        then: "pipe start should be invoked"
        startResult == "started"

        when: "pipe call start second time"
        startResult = easyPipeRegistry.start(PIPE_NAME)

        then: "pipe start should be invoked"
        startResult == "pipe is running"

        when: "pipe call stop"
        def stopResult = easyPipeRegistry.stop(PIPE_NAME)

        then: "pipe stop should be invoked"
        stopResult == "stopped"

        when: "pipe call stop second time"
        stopResult = easyPipeRegistry.stop(PIPE_NAME)

        then: "pipe stop should be invoked"
        stopResult == "pipe is not running"
    }

    def "should not add an pipes with incorrect configuration"() {
        given: "incorrect pipe instance"
        def incorrectPipe = Mock(EasyPipe)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put(INCORRECT_PIPE_NAME, incorrectPipe)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> [:]
        def incorrectBeanDefinition = Mock(BeanDefinition)
        incorrectBeanDefinition.getSource() >> Mock(StandardAnnotationMetadata)

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(INCORRECT_PIPE_NAME) >> incorrectBeanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "incorrect pipe call start"
        def startResult = easyPipeRegistry.start(INCORRECT_PIPE_NAME)

        then: "incorrect pipe start should not be invoked"
        startResult == "Pipe $INCORRECT_PIPE_NAME doesn't registered"
        0 * incorrectPipe.start()

        when: "incorrect pipe call stop"
        def stopResult = easyPipeRegistry.stop(INCORRECT_PIPE_NAME)

        then: "pipe stop should be invoked"
        stopResult == "Pipe $INCORRECT_PIPE_NAME doesn't registered"
        0 * incorrectPipe.stop()
    }

    def "should not add pipes with same names"() {
        given: "pipe instance"
        def testPipe1 = Mock(EasyPipe)
        def testPipe2 = Mock(EasyPipe)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put("pipe1", testPipe1)
            pipesBeans.put("pipe2", testPipe2)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("value", PIPE_NAME)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes
        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(_ as String) >> beanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        then: "pipe call start"
        thrown BeanCreationException
    }

    def "should clean up pipe definition after pipe is break"() {
        given: "pipe instance with broken start method"
        def testPipe = Mock(EasyPipe)
        testPipe.start() >> {throw new RuntimeException()}

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, testPipe)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("value", PIPE_NAME)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes
        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(PIPE_NAME) >> beanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipe call start"
        def startResult = easyPipeRegistry.start(PIPE_NAME)

        and: "wait for a half second"
        sleep 500

        then: "result should indicate that pipe is broken"
        startResult == "started"

        when: "pipe call start"
        startResult = easyPipeRegistry.start(PIPE_NAME)

        then: "result should indicate that pipe is broken"
        startResult == "started"
    }

    def "should handle broken pipe stops"() {
        given: "pipe instance"
        def testPipe = Mock(EasyPipe)
        testPipe.stop() >> {throw new RuntimeException()}

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(EasyPipe.class, true, true) >> {
            Map<String, EasyPipe> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, testPipe)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("value", PIPE_NAME)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes
        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata

        and: "Spring beans factory"
        def beanFactory = Mock(ConfigurableListableBeanFactory)
        beanFactory.getBeanDefinition(PIPE_NAME) >> beanDefinition

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipe call start"
        def startResult = easyPipeRegistry.start(PIPE_NAME)

        then: "pipe start should be invoked"
        startResult == "started"

        when: "pipe call stop"
        def stopResult = easyPipeRegistry.stop(PIPE_NAME)

        then: "pipe stop should be invoked"
        stopResult == "failed to stop"
    }
}
