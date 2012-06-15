package com.es;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdConfiguration extends AbstractConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(CmdConfiguration.class);
    
    public static final String PGN_FILE = "pgn-file";
    public static final String TRANSPOSITION_TABLE_SIZE = "trans-table-size";
    public static final String MODE = "mode";
    public static final String DEPTH = "depth";
    
    private Options options;
    private CommandLine commandLine = null;
    private List<String> keys = null;
    
    public CmdConfiguration() {
        this.options = new Options();

        @SuppressWarnings("static-access")
        final Option fileOption = OptionBuilder.withLongOpt("PGN_FILE")
                                               .hasArg()
                                               .withArgName("game.pgn")
                                               .withDescription("PGN file including a full or partial game. Default is none.")
                                               .create("f");
        
        @SuppressWarnings("static-access")
        final Option transOption = OptionBuilder.withLongOpt(TRANSPOSITION_TABLE_SIZE)
                                                .hasArg()
                                                .withDescription("The number of boards to store in the transposition table. Default is 100,000")
                                                .create("t");

        @SuppressWarnings("static-access")
        final Option modeOption = OptionBuilder.withLongOpt(MODE)
                                               .hasArg()
                                               .withDescription("The mode to run the engine in: GUI or UCI. Default is GUI.")
                                               .create("m");

        @SuppressWarnings("static-access")
        final Option depthOption = OptionBuilder.withLongOpt(DEPTH)
                                                .hasArg()
                                                .withDescription("The number of nodes to search")
                                                .create("d");

        // add all the options from above
        options.addOption(fileOption);
        options.addOption(transOption);
        options.addOption(modeOption);
        options.addOption(depthOption);

        // add new simple options
        options.addOption(new Option("h", "help", false, "Print this help message"));
    }
    
    public boolean parse(String[] args) {
        final CommandLineParser parser = new PosixParser();

        try {
            commandLine = parser.parse(options, args);
        } catch (final ParseException e) {
            LOG.error("Error parsing the command line", e);
            return false;
        }
        
        return true;
    }
    
    public void printHelp() {
        new HelpFormatter().printHelp("chess", options);
    }
    
    private void constructKeysList() {
        if(keys == null) {
            Option[] opts = commandLine.getOptions();
            keys = new ArrayList<String>(opts.length);
            
            for(Option opt:opts) {
                keys.add(opt.getArgName());
            }
        }
    }

    public boolean containsKey(String arg) {
        return commandLine.hasOption(arg);
    }

    public Iterator<String> getKeys() {
        constructKeysList();
        
        return keys.iterator();
    }

    public Object getProperty(String arg) {
            return commandLine.getOptionValue(arg);
    }

    public boolean isEmpty() {
        constructKeysList();

        return keys.isEmpty();
    }

    @Override
    protected void addPropertyDirect(String arg0, Object arg1) {
        // cannot add properties to this configuration
    }
    
}
