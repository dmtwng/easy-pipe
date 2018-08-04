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

    private pipelineContext
    private applicationContext
    private beanFactory

    void setup() {
        // Correct pipeline setup
        given: "pipe instance"
        pipelineContext = Mock(PipelineContext)

        and: "application context with bean definition"
        applicationContext = Mock(ApplicationContext)
        this.applicationContext.getBeansOfType(PipelineContext.class, true, true) >> {
            Map<String, PipelineContext> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, this.pipelineContext)
            return pipesBeans
        }

        and: "bean definition of pipeline"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("name", PIPE_NAME)
        attributes.put("autostart", false)
        annotatedTypeMetadata.getAnnotationAttributes(_ as String) >> attributes
        def beanDefinition = Mock(BeanDefinition)
        beanDefinition.getSource() >> annotatedTypeMetadata

        and: "Spring beans factory"
        beanFactory = Mock(ConfigurableListableBeanFactory)
        this.beanFactory.getBeanDefinition(PIPE_NAME) >> beanDefinition
    }


    def "should add correct pipeline with annotation"() {
        given: "pipe instance with pending status"
        pipelineContext.getStatus() >> PipelineContext.Status.PENDING
        pipelineContext.start() >> true

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        then: "test pipeline should be registered and pending"
        easyPipeRegistry.pipesList().keySet().contains(PIPE_NAME)
        easyPipeRegistry.status(PIPE_NAME) == PipelineContext.Status.PENDING.name

        when: "such pipeline starting"
        def response = easyPipeRegistry.start(PIPE_NAME)

        then: "pipe should be abe to start"
        response == "started"
    }

    def "should stop running pipelines"() {
        given: "pipe instance with running status"
        pipelineContext.getStatus() >> PipelineContext.Status.RUNNING
        pipelineContext.stop() >> true

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipeline stopping"
        def response = easyPipeRegistry.stop(PIPE_NAME)

        then: "running pipeline should not be started again"
        response == "stopped"
    }

    def "should not start already running pipelines"() {
        given: "pipe instance with running status"
        pipelineContext.getStatus() >> PipelineContext.Status.RUNNING

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipeline is starting"
        def response = easyPipeRegistry.start(PIPE_NAME)

        then: "running pipeline should not be started again"
        response == "pipe is running"
    }

    def "should not stop pending pipelines"() {
        given: "pipe instance with running status"
        pipelineContext.getStatus() >> PipelineContext.Status.PENDING

        and: "easy pipes registry"
        def easyPipeRegistry = new EasyPipeRegistry(applicationContext, beanFactory)

        when: "post construct invoked"
        easyPipeRegistry.buildPipe()

        and: "pipeline is starting"
        def response = easyPipeRegistry.stop(PIPE_NAME)

        then: "running pipeline should not be started again"
        response == "pipe is not running"
    }

    def "should not add an pipes with incorrect configuration"() {
        given: "incorrect pipe instance"
        def incorrectPipe = Mock(PipelineContext)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(PipelineContext.class, true, true) >> {
            Map<String, PipelineContext> pipesBeans = new HashMap<>()
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

        then: "incorrect pipe start should not be found"
        easyPipeRegistry.start(INCORRECT_PIPE_NAME) == "Pipe $INCORRECT_PIPE_NAME doesn't registered"
        easyPipeRegistry.stop(INCORRECT_PIPE_NAME) == "Pipe $INCORRECT_PIPE_NAME doesn't registered"
        easyPipeRegistry.status(INCORRECT_PIPE_NAME) == "Pipe $INCORRECT_PIPE_NAME doesn't registered"
    }

    def "should not add pipes with same names"() {
        given: "pipe instance"
        def testPipe1 = Mock(PipelineContext)
        def testPipe2 = Mock(PipelineContext)

        and: "application context with two bean definitions"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(PipelineContext.class, true, true) >> {
            Map<String, PipelineContext> pipesBeans = new HashMap<>()
            pipesBeans.put("pipe1", testPipe1)
            pipesBeans.put("pipe2", testPipe2)
            return pipesBeans
        }

        and: "bean definition of pipes"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("name", PIPE_NAME)
        attributes.put("autostart", false)
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

    def "should start autostartable pipelines"() {
        given: "pipe instance"
        def autostartPipeline = Mock(PipelineContext)
        autostartPipeline.getStatus() >> PipelineContext.Status.PENDING
        autostartPipeline.start() >> true

        and: "application context with bean definition"
        def applicationContext = Mock(ApplicationContext)
        applicationContext.getBeansOfType(PipelineContext.class, true, true) >> {
            Map<String, PipelineContext> pipesBeans = new HashMap<>()
            pipesBeans.put(PIPE_NAME, autostartPipeline)
            return pipesBeans
        }

        and: "bean definition of pipeline"
        def annotatedTypeMetadata = Mock(StandardAnnotationMetadata)
        Map<String, Object> attributes = new HashMap<>()
        attributes.put("name", PIPE_NAME)
        attributes.put("autostart", true)
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

        then: "test pipeline should be registered and pending"
        easyPipeRegistry.pipesList().keySet().contains(PIPE_NAME)
        1 * autostartPipeline.start()
    }
}
