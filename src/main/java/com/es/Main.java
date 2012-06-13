package com.es;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.es.engines.Engine;
import com.es.engines.GuiEngine;

public class Main {

    /**
     * @param args
     * @throws ConfigurationException 
     */
    public static void main(String[] args) throws ConfigurationException {
        final CmdConfiguration cmdConfig = new CmdConfiguration();
        final CompositeConfiguration config = new CompositeConfiguration();
        
        // parse the command line
        cmdConfig.parse(args);
        
        // take properties from the command line first
        config.addConfiguration(cmdConfig);
        // then from a configuration file
        config.addConfiguration(new PropertiesConfiguration("chess.properties"));
        // finally the defaults
        config.addConfiguration(configureDefaults());
        
        // get the mode for the program to run in
        final String mode = config.getString(CmdConfiguration.MODE);
        
        Engine engine = null;
        
        if("GUI".equalsIgnoreCase(mode)) {
            engine = new GuiEngine(config);
        } else if("UCI".equalsIgnoreCase(mode)) {
            // engine = new UciEngine(config);
        } else {
            cmdConfig.printHelp();
            throw new ConfigurationException("A mode of " + mode + " is not supported");
        }
        
        engine.play();  // start the game
    }
    
    private static BaseConfiguration configureDefaults() {
        BaseConfiguration defaults = new BaseConfiguration();
        
        defaults.addProperty(CmdConfiguration.TRANSPOSITION_TABLE_SIZE, 100000);
        defaults.addProperty(CmdConfiguration.MODE, "GUI");

        return defaults;
    }
    

}
