package ru.spbau.bocharov.ftp.client;

import org.apache.commons.cli.*;
import ru.spbau.bocharov.ftp.client.commands.Command;
import ru.spbau.bocharov.ftp.client.commands.CommandFactory;
import ru.spbau.bocharov.ftp.net.NetworkManager;

import java.net.InetAddress;
import java.net.Socket;

public class FTPClient {

    private static final String PORT_ARG_NAME = "port";
    private static final String ADDR_ARG_NAME = "address";
    private static final String CMD_ARG_NAME  = "command";
    private static final Options OPTIONS = new Options();
    static {
        OPTIONS.addOption(PORT_ARG_NAME, true, "ftp server port");
        OPTIONS.addOption(ADDR_ARG_NAME, true, "ftp server ip address");
        OPTIONS.addOption(CMD_ARG_NAME,  true, "command to run");
    }

    public static void main(String[] args) {
        try {
            CommandLine cmdLine = parseArgs(args);
            Socket socket = new NetworkManager().createSocket(
                    InetAddress.getByName(cmdLine.getOptionValue(ADDR_ARG_NAME)),
                    Integer.valueOf(cmdLine.getOptionValue(PORT_ARG_NAME)));

            String cmdName = cmdLine.getOptionValue(CMD_ARG_NAME);
            Command cmd = CommandFactory.getInstance().createCommand(cmdName);
            cmd.execute(socket, cmdLine.getArgs());
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(OPTIONS, args);

        if (!cmdLine.hasOption(PORT_ARG_NAME)) {
            throw new ParseException("you should specify server port");
        }
        if (!cmdLine.hasOption(ADDR_ARG_NAME)) {
            throw new ParseException("you should specify server address");
        }
        if (!cmdLine.hasOption(CMD_ARG_NAME)) {
            throw new ParseException("you should specify command");
        }

        return cmdLine;
    }
}
