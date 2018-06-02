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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        this.pipeDefinitions = new ConcurrentHashMap<>();
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
        if (!pipeRegistered(pipeName)) {
            return String.format("Pipe %s doesn't registered", pipeName);
        }
        if (pipeIsRunning(pipeName)) {
            return "pipe is running";
        }

        return  startPipe(pipeName) ? "started" : "failed to start";
    }

    @GET
    @Produces("text/plain")
    @Path("/{pipeName}/stop")
    public String stop(@PathParam("pipeName") String pipeName) {
        if (!pipeRegistered(pipeName)) {
            return String.format("Pipe %s doesn't registered", pipeName);
        }
        if (!pipeIsRunning(pipeName)) {
            return "pipe is not running";
        }

        return stopPipe(pipeName) ? "stopped" : "failed to stop";
    }


    private boolean startPipe(String pipeName) {
        PipeDefinition pipeDefinition = pipeDefinitions.get(pipeName);
        try {
            pipeDefinition.setRunnable(new PipeRunnable(pipeDefinition.getPipe()));
            pipeDefinition.setThread(new Thread(pipeDefinition.getRunnable()));
            pipeDefinition.getThread().setUncaughtExceptionHandler(new PipeThreadExceptionHandler(pipeName));
            pipeDefinition.getThread().start();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start EasyPipe with name {0}", pipeName, e);
            return false;
        }
        return true;
    }

    private boolean stopPipe(String pipeName) {
        PipeDefinition pipeDefinition = pipeDefinitions.get(pipeName);
        try {
            pipeDefinition.getPipe().stop();
            pipeDefinition.setRunnable(null);
            pipeDefinition.setThread(null);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start EasyPipe with name {0}", pipeName, e);
            return false;
        }
        return true;
    }

    private boolean pipeRegistered(String pipeName) {
        return pipeDefinitions.containsKey(pipeName);
    }

    private boolean pipeIsRunning(String pipeName) {
        return pipeDefinitions.get(pipeName).getThread() != null;
    }


    private class PipeThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        private String pipeName;

        public PipeThreadExceptionHandler(String pipeName) {
            this.pipeName = pipeName;
        }

        @Override
        public synchronized void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Pipe {} failed", pipeName, e);

            PipeDefinition definition = pipeDefinitions.get(pipeName);
            definition.setRunnable(null);
            definition.setThread(null);
        }
    }

}
