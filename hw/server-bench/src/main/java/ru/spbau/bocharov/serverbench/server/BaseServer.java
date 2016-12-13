package ru.spbau.bocharov.serverbench.server;

import java.io.IOException;
import java.net.SocketException;

public interface BaseServer {

    void start() throws SocketException;

    void stop() throws IOException, InterruptedException;
}
