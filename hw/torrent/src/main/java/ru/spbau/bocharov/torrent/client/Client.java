package ru.spbau.bocharov.torrent.client;

import org.apache.commons.cli.*;
import ru.spbau.bocharov.torrent.client.gui.MainWindow;
import ru.spbau.bocharov.torrent.common.FileInfo;
import ru.spbau.bocharov.torrent.common.InvalidHandlerStateException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static final String PORT_ARG_NAME = "port";
    private static final String TRACKER_ADDR_ARG_NAME = "tracker";
    private static final String DIR_ARG_NAME = "directory";
    private static final String STATE_ARG_NAME = "state";
    private static final String ENABLE_GUI_MODE = "gui";
    private static final Options OPTIONS = new Options();
    static {
        OPTIONS.addOption(PORT_ARG_NAME, true, "local port to start tracker");
        OPTIONS.addOption(TRACKER_ADDR_ARG_NAME, true, "server host address");
        OPTIONS.addOption(DIR_ARG_NAME, true, "directory to put loaded files");
        OPTIONS.addOption(STATE_ARG_NAME, true, "file with tracker state");
        OPTIONS.addOption(ENABLE_GUI_MODE, false, "enable gui");
    }


    public static void main(String[] args) throws ParseException, IOException, InvalidHandlerStateException, InterruptedException {

        CommandLine cmd = parseArgs(args);

        Storage storage = new Storage(cmd.getOptionValue(DIR_ARG_NAME));
        InetSocketAddress trackerAddress = new InetSocketAddress(cmd.getOptionValue(TRACKER_ADDR_ARG_NAME), 8081);

        Short port = Short.parseShort(cmd.getOptionValue(PORT_ARG_NAME));
        TorrentClient client = new TorrentClient(port, trackerAddress, storage, cmd.getOptionValue(STATE_ARG_NAME));

        if (cmd.hasOption(ENABLE_GUI_MODE)) {
            runGui(client);
        } else {
            runConsole(client);
        }
    }

    private static void runGui(TorrentClient client) {
        SwingUtilities.invokeLater(() -> {
            try {
                client.start();
                MainWindow window = new MainWindow(client);
                client.addDownloadStateListener(window);
                window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                window.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        try {
                            client.stop();
                        } catch (InterruptedException | IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {

                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });
                window.setVisible(true);
            } catch (IOException | InvalidHandlerStateException e) {
                e.printStackTrace();
            }
        });
    }

    private static void runConsole(TorrentClient client) throws IOException, InvalidHandlerStateException, InterruptedException {
        client.start();
        boolean stopped = false;
        while (!stopped) {
            try {
                String[] cmdArg = getUserInput().split(" ");
                switch (cmdArg[0]) {
                    case "list":
                        printFiles(client.list());
                        break;
                    case "get":
                        client.get(Integer.parseInt(cmdArg[1]));
                        System.out.println("File enqueued");
                        break;
                    case "upload":
                        int fileId = client.upload(cmdArg[1]);
                        System.out.println(String.format("Uploaded with id = %d", fileId));
                        break;
                    case "quit":
                        stopped = true;
                        break;
                    default:
                        System.err.println("unknown command");
                }
            } catch (Exception e) {
                System.err.println(String.format("error: %s", e.getMessage()));
            }
        }
        client.stop();
    }

    private static String getUserInput() {
        return new Scanner(System.in).nextLine();
    }

    private static void printFiles(List<FileInfo> files) {
        System.out.println(String.format("%4s|%20s|%8s", "ID", "NAME", "SIZE"));
        System.out.println("------------------------------------");
        for (FileInfo file: files) {
            System.out.println(String.format("%4d|%20s|%8d",
                    file.getFileId(), file.getFileName(), file.getSize()));
            System.out.println("------------------------------------");
        }
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(OPTIONS, args);

        if (!cmdLine.hasOption(PORT_ARG_NAME)) {
            throw new ParseException("you should specify port");
        }
        if (!cmdLine.hasOption(TRACKER_ADDR_ARG_NAME)) {
            throw new ParseException("you should specify tracker address");
        }
        if (!cmdLine.hasOption(DIR_ARG_NAME)) {
            throw new ParseException("you should specify directory");
        }

        return cmdLine;
    }
}
