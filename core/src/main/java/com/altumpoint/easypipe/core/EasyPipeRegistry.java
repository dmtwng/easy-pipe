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
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for all EasyPipe pipelines in application context.
 *
 * @since 0.1.0
 */
@Path("easy-pipes")
@Component
public class EasyPipeRegistry {
    private static final String TMPL_PIPE_DOESNT_REGISTERED = "Pipe %s doesn't registered";

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPipeRegistry.class);

    private ApplicationContext applicationContext;

    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, EasyPipeInfo> pipeInfoMap;


    @Autowired
    public EasyPipeRegistry(ApplicationContext applicationContext, ConfigurableListableBeanFactory beanFactory) {
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
        this.pipeInfoMap = new ConcurrentHashMap<>();
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
        if (pipeInfoMap.containsKey(name)) {
            throw new BeanCreationException(String
                    .format("Failed to create EasyPipe Registry: pipe with name %s already registered", name));
        }

        LOGGER.info("EasyPipe Registry: registering pipe '{}'", name);
        EasyPipeInfo easyPipeInfo = new EasyPipeInfo();
        easyPipeInfo.setPipe(pipe);
        easyPipeInfo.setStatus(EasyPipeInfo.Status.PENDING);
        pipeInfoMap.put(name, easyPipeInfo);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> pipesList() {
        return pipeInfoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getStatus().name));
    }

    @GET
    @Path("/{pipeName}/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start(@PathParam("pipeName") String pipeName) {
        if (!pipeRegistered(pipeName)) {
            return String.format(TMPL_PIPE_DOESNT_REGISTERED, pipeName);
        }
        if (pipeIsRunning(pipeName)) {
            return "pipe is running";
        }

        return  startPipe(pipeName) ? "started" : "failed to start";
    }

    @GET
    @Path("/{pipeName}/stop")
    @Produces(MediaType.TEXT_PLAIN)
    public String stop(@PathParam("pipeName") String pipeName) {
        if (!pipeRegistered(pipeName)) {
            return String.format(TMPL_PIPE_DOESNT_REGISTERED, pipeName);
        }
        if (!pipeIsRunning(pipeName)) {
            return "pipe is not running";
        }

        return stopPipe(pipeName) ? "stopped" : "failed to stop";
    }

    @GET
    @Path("/{pipeName}/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String status(@PathParam("pipeName") String pipeName) {
        if (!pipeRegistered(pipeName)) {
            return String.format(TMPL_PIPE_DOESNT_REGISTERED, pipeName);
        }

        return pipeInfoMap.get(pipeName).getStatus().name;
    }


    private boolean startPipe(String pipeName) {
        EasyPipeInfo easyPipeInfo = pipeInfoMap.get(pipeName);
        try {
            Thread pipeThread = new Thread(new PipeRunnable(easyPipeInfo.getPipe()));
            pipeThread.setUncaughtExceptionHandler(new PipeThreadExceptionHandler(pipeName));
            pipeThread.start();
            easyPipeInfo.setStatus(EasyPipeInfo.Status.RUNNING);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start EasyPipe with name {0}", pipeName, e);
            easyPipeInfo.setStatus(EasyPipeInfo.Status.FAILED);
            return false;
        }
        return true;
    }

    private boolean stopPipe(String pipeName) {
        EasyPipeInfo easyPipeInfo = pipeInfoMap.get(pipeName);
        try {
            easyPipeInfo.getPipe().stop();
            easyPipeInfo.setStatus(EasyPipeInfo.Status.PENDING);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start EasyPipe with name {0}", pipeName, e);
            easyPipeInfo.setStatus(EasyPipeInfo.Status.FAILED);
            return false;
        }
        return true;
    }

    private boolean pipeRegistered(String pipeName) {
        return pipeInfoMap.containsKey(pipeName);
    }

    private boolean pipeIsRunning(String pipeName) {
        return pipeInfoMap.get(pipeName).getStatus() == EasyPipeInfo.Status.RUNNING;
    }


    /**
     * Exception handler for pipes threads.
     * In case of exception, changes status of pipe to {@code FAILED}.
     */
    private class PipeThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        private String pipeName;

        public PipeThreadExceptionHandler(String pipeName) {
            this.pipeName = pipeName;
        }

        @Override
        public synchronized void uncaughtException(Thread t, Throwable e) {
            LOGGER.error("Pipe {} failed", pipeName, e);

            EasyPipeInfo pipeInfo = pipeInfoMap.get(pipeName);
            pipeInfo.setStatus(EasyPipeInfo.Status.FAILED);
        }
    }

}
