package com.es;

import jcpi.standardio.StandardIoCommunication;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.es.engines.Engine;
import com.es.engines.GuiEngine;
import com.es.engines.UciEngine;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

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

        try {
            // then from a configuration file
            config.addConfiguration(new PropertiesConfiguration("chess.properties"));
        } catch(ConfigurationException e) {
            LOG.warn("No chess.properties file found");
        }

        // finally the defaults
        config.addConfiguration(configureDefaults());

        // get the mode for the program to run in
        final String mode = config.getString(CmdConfiguration.MODE);

        Engine engine = null;

        if("GUI".equalsIgnoreCase(mode)) {
            engine = new GuiEngine(config);
        } else if("UCI".equalsIgnoreCase(mode)) {
            engine = new UciEngine(config, new StandardIoCommunication());
        } else {
            cmdConfig.printHelp();
            throw new ConfigurationException("A mode of " + mode + " is not supported");
        }

        try {
            engine.play();  // start the game
        } catch(Exception e) {
            LOG.error("Caught exception in play()", e);
            if("GUI".equalsIgnoreCase(mode)) {
                System.err.println("Caught exception in play()");
            }
        }
    }

    private static BaseConfiguration configureDefaults() {
        BaseConfiguration defaults = new BaseConfiguration();

        defaults.addProperty(CmdConfiguration.TRANSPOSITION_TABLE_SIZE, 100000);
        defaults.addProperty(CmdConfiguration.MODE, "GUI");
        defaults.addProperty(CmdConfiguration.DEPTH, 4);

        return defaults;
    }


}
