package ru.spbau.bocharov.serverbench.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.common.ServerType;
import ru.spbau.bocharov.serverbench.server.util.ServerStatistics;
import ru.spbau.bocharov.serverbench.util.FactoryException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Bootstrap {

    private static final Logger log = LogManager.getLogger(Bootstrap.class);

    private static final ServerFactory factory = ServerFactory.getInstance();

    void start(int port) throws IOException {
        ServerStatistics stats = ServerStatistics.getInstance();
        try (ServerSocket ss = new ServerSocket(port)) {

            log.info("started bootstraper on port " + port);
            while (!ss.isClosed()) {
                try (Socket client = ss.accept();
                     DataInputStream in = new DataInputStream(client.getInputStream());
                     DataOutputStream out = new DataOutputStream(client.getOutputStream())) {

                    log.info("accepted client from " +
                            client.getInetAddress().getHostName() + ":" + client.getPort());
                    stats.clear();

                    String serverType = in.readUTF();
                    int serverPort = in.readInt();

                    BaseServer server;
                    try {
                        server = factory.create(ServerType.valueOf(serverType), serverPort);
                        out.writeUTF("OK");
                    } catch (FactoryException e) {
                        log.error("failed to create server: " + e.getMessage());
                        e.printStackTrace();
                        out.writeUTF("failed to create server: " + e.getMessage());
                        continue;
                    }

                    log.info("running server of type " + serverType);
                    server.start();

                    int msg = in.readInt();
                    server.stop();

                    out.writeLong(stats.getClientProcessingTime());
                    out.writeLong(stats.getRequestProcessingTime());
                } catch (InterruptedException e) {
                    log.warn("server interrupted while stopping");
                }
            }
        }
    }
}