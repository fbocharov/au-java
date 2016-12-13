package ru.spbau.bocharov.serverbench.server.impl.udp;

import ru.spbau.bocharov.serverbench.server.BaseServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class BaseUDPServer implements BaseServer {

    private static final int BUFFER_SIZE = 128 * 1024 * 1024; // 128 kb

    private DatagramSocket ssocket;
    private final Thread selfThread;

    public BaseUDPServer(short port) {
        selfThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                ssocket = socket;
                byte[] buffer = new byte[BUFFER_SIZE];

                while (!ssocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    ssocket.receive(packet);
                    handle(ssocket, packet);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public void start() throws SocketException {
        if (ssocket != null) {
            throw new SocketException("server already running");
        }

        selfThread.start();
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        if (ssocket == null) {
            throw new SocketException("server not running");
        }

        ssocket.close();
        selfThread.join();
        ssocket = null;
    }

    protected abstract void handle(DatagramSocket socket, DatagramPacket packet);
}
