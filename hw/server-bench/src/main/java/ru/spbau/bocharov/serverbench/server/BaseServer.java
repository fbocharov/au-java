package ru.spbau.bocharov.serverbench.server;

import java.io.IOException;
import java.net.SocketException;

public abstract class BaseServer {

    private boolean serverStarted;
    private final Thread selfThread;

    public BaseServer(int port) {
        selfThread = new Thread(createServer(port));
    }

    public final void start() {
        if (serverStarted) {
            throw new ServerException("server already started");
        }

        selfThread.start();
        serverStarted = true;
    }

    public final void stop() throws IOException, InterruptedException {
        if (!serverStarted) {
            throw new ServerException("server not running");
        }

        shutdownServer();
        selfThread.join();
        serverStarted = false;
    }

    protected abstract Runnable createServer(int port);

    protected abstract void shutdownServer() throws IOException, InterruptedException;
}
