package ru.spbau.bocharov.ftp.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.spbau.bocharov.ftp.net.NetworkManager;

import java.io.DataInputStream;
import java.util.Objects;
import java.util.Scanner;

public class ServerMain {

    private static final String PORT_ARG_NAME = "port";
    private static final String DIR_ARG_NAME  = "directory";
    private static final Options options = new Options();
    static {
        options.addOption(PORT_ARG_NAME, true, "ftp server port");
        options.addOption(DIR_ARG_NAME,  true, "directory where to run server");
    }

    public static void main(String[] args) {
        try {
            CommandLine cmdLine = parserArgs(args);

            FTPServer server = new FTPServer(new NetworkManager(), cmdLine.getOptionValue(DIR_ARG_NAME));

            server.start(Integer.valueOf(cmdLine.getOptionValue(PORT_ARG_NAME)));

            String input;
            Scanner in = new Scanner(System.in);
            do {
                input = in.nextLine();
            } while (!Objects.equals(input, ":q"));

            server.stop();

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private static CommandLine parserArgs(String[] args) throws ParseException {
        CommandLine cmdLine = new DefaultParser().parse(options, args);

        if (!cmdLine.hasOption(PORT_ARG_NAME)) {
            throw new ParseException("you should specify port");
        }
        if (!cmdLine.hasOption(DIR_ARG_NAME)) {
            throw new ParseException("you should specify directory");
        }

        return cmdLine;
    }
}
