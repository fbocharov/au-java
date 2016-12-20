package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.server.BaseServer;
import ru.spbau.bocharov.serverbench.server.ServerException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseTCPServer extends BaseServer {

    private static final Logger log = LogManager.getLogger(BaseTCPServer.class);

    private ServerSocket ssocket;

    BaseTCPServer(int port) {
        super(port);
    }


    @Override
    protected Runnable createServer(int port) {
        return () -> {
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
        };
    }

    @Override
    protected void shutdownServer() throws IOException, InterruptedException {
        ssocket.close();
    }

    protected abstract void handle(Socket client, Job job);
}
