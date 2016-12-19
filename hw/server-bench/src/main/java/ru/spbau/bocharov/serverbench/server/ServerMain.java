package ru.spbau.bocharov.serverbench.server;

import org.apache.commons.cli.*;

import java.io.IOException;

public class ServerMain {

    private static final String PORT_ARG_NAME = "port";
    private static final Options OPTIONS = new Options();
    static {
        OPTIONS.addOption(PORT_ARG_NAME, true, "local port to start server");
    }

    public static void main(String[] args) {
        try {
            CommandLine cmd = parseArgs(args);
            int port = Integer.valueOf(cmd.getOptionValue(PORT_ARG_NAME));
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.start(port);
        } catch (IOException e) {
            System.err.println("failed to run server: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("failed to parser args: " + e.getMessage());
        }
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(OPTIONS, args);

        if (!cmdLine.hasOption(PORT_ARG_NAME)) {
            throw new ParseException("you should specify port");
        }

        return cmdLine;
    }
}
