package com.altumpoint.easypipe.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all EasyPipe pipelines in application context.
 *
 * @since 0.1.0
 */
@Path("easy-pipes")
@Component
public class EasyPipeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPipeRegistry.class);

    private ApplicationContext applicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, PipeDefinition> pipeDefinitions;


    @Autowired
    public EasyPipeRegistry(ApplicationContext applicationContext, ConfigurableListableBeanFactory beanFactory) {
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
        this.pipeDefinitions = new HashMap<>();
    }

    @PostConstruct
    public void buildPipe() {
        Map<String, EasyPipe> pipes = applicationContext.getBeansOfType(EasyPipe.class, true, true);
        for (Map.Entry<String, EasyPipe> entry : pipes.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if(beanDefinition.getSource() instanceof AnnotatedTypeMetadata) {
                AnnotatedTypeMetadata metadata = (AnnotatedTypeMetadata) beanDefinition.getSource();
                Map<String, Object> attributes = metadata.getAnnotationAttributes(EasyPipeComponent.class.getName());
                if (attributes != null && attributes.containsKey("value")) {
                    String annotationValue = (String) attributes.get("value");
                    registerPipe("".equals(annotationValue) ? beanName : annotationValue, entry.getValue());
                }
            }
        }
    }

    private void registerPipe(String name, EasyPipe pipe) {
        if (pipeDefinitions.containsKey(name)) {
            throw new BeanCreationException(String
                    .format("Failed to create EasyPipe Registry: pipe with name %s already registered", name));
        }

        LOGGER.info("EasyPipe Registry: registering pipe '{}'", name);
        PipeDefinition pipeDefinition = new PipeDefinition();
        pipeDefinition.setPipe(pipe);
        pipeDefinitions.put(name, pipeDefinition);
    }

    @GET
    @Produces("text/plain")
    @Path("/{pipeName}/start")
    public String start(@PathParam("pipeName") String pipeName) {
        if (!pipeDefinitions.containsKey(pipeName)) {
            return String.format("Pipe %s doesn't registered", pipeName);
        }
        PipeDefinition pipeDefinition = pipeDefinitions.get(pipeName);
        if (pipeDefinition.getThread() != null) {
            return "pipe is running";
        }

        pipeDefinition.setRunnable(new PipeRunnable(pipeDefinition.getPipe()));
        pipeDefinition.setThread(new Thread(pipeDefinition.getRunnable()));
        pipeDefinition.getThread().start();
        return "started";
    }

    @GET
    @Produces("text/plain")
    @Path("/{pipeName}/stop")
    public String stop(@PathParam("pipeName") String pipeName) throws InterruptedException {
        if (!pipeDefinitions.containsKey(pipeName)) {
            return String.format("Pipe %s doesn't registered", pipeName);
        }
        PipeDefinition pipeDefinition = pipeDefinitions.get(pipeName);
        if (pipeDefinition.getThread() == null) {
            return "pipe is not running";
        }

        pipeDefinition.getRunnable().terminate();
        pipeDefinition.getThread().join();
        pipeDefinition.setRunnable(null);
        pipeDefinition.setThread(null);
        return "stopped";
    }

}
