package com.altumpoint.easypipe.demo;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages("com.altumpoint.easypipe.demo", "com.altumpoint.easypipe.core");
    }

}
