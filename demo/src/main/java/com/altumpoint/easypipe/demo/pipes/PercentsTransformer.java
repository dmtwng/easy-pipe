package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.steps.EasyTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PercentsTransformer implements EasyTransformer<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PercentsTransformer.class);

    private static final String PERCENTS_FORMAT = "%d %%";

    public String transform(String message) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(400, 550));
        } catch (InterruptedException e) {
            LOGGER.warn("Transformer was interrupted.", e);
            Thread.currentThread().interrupt();
        }
        String transformedResult = String.format(PERCENTS_FORMAT, Math.round(Double.parseDouble(message) * 100));
        LOGGER.info("Transformed message {} to {}", message, transformedResult);
        return transformedResult;
    }
}
