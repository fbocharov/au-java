package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.server.BaseServer;
import ru.spbau.bocharov.serverbench.server.ServerException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseTCPServer implements BaseServer {

    private static final Logger log = LogManager.getLogger(BaseTCPServer.class);

    private ServerSocket ssocket;
    private final Thread selfThread;

    BaseTCPServer(int port) {
        selfThread = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                ssocket = ss;

                log.info("started server on port " + port);
                while (!ssocket.isClosed()) {
                    Socket client = ssocket.accept();
                    log.info("accepted client from " +
                            client.getInetAddress().getHostName() + ":" + client.getPort());
                    handle(client, new Job());
                }
            } catch (IOException e) {
                log.error("io error occured: " + e.getMessage());
                throw new UncheckedIOException(e);
            }
        });
    }

    public void start() {
        if (ssocket != null) {
            throw new ServerException("server already started");
        }

        selfThread.start();
    }

    public void stop() throws IOException, InterruptedException {
        if (ssocket == null) {
            throw new ServerException("server not running");
        }

        ssocket.close();
        selfThread.join();
        ssocket = null;
    }

    protected abstract void handle(Socket client, Job job);
}
