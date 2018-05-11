package com.altumpoint.easypipe.demo.pipes;

import com.altumpoint.easypipe.core.steps.EasyTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PercentsTransformer implements EasyTransformer<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PercentsTransformer.class);

    private static final String PERCENTS_FORMAT = "%d %%";

    public String transform(String message) {
        String transformedResult = String.format(PERCENTS_FORMAT, Math.round(Double.parseDouble(message) * 100));
        LOGGER.info("Transformed message {} to {}", message, transformedResult);
        return transformedResult;
    }
}
