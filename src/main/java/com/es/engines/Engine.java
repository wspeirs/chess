package com.es.engines;

import org.apache.commons.configuration.Configuration;

public abstract class Engine {
    
    private Configuration config;
    
    public Engine(Configuration config) {
        this.config = config;
    }
    
    public Configuration getConfig() {
        return config;
    }
    
    public abstract void play();
}
