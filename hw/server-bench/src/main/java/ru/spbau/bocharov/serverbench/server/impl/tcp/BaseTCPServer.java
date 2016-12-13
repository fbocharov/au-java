package ru.spbau.bocharov.serverbench.server.impl.tcp;

import ru.spbau.bocharov.serverbench.server.BaseServer;
import ru.spbau.bocharov.serverbench.server.ServerException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class BaseTCPServer implements BaseServer {

    private ServerSocket ssocket;
    private final Thread selfThread;

    BaseTCPServer(int port) {
        selfThread = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                ssocket = ss;

                while (!ssocket.isClosed()) {
                    Socket client = ssocket.accept();
                    handle(client, new Job());
                }
            } catch (IOException e) {
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
