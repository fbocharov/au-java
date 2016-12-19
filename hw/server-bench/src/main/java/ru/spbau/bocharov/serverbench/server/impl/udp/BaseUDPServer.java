package ru.spbau.bocharov.serverbench.server.impl.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.server.BaseServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class BaseUDPServer implements BaseServer {

    private static final Logger log = LogManager.getLogger(BaseUDPServer.class);

    private static final int BUFFER_SIZE = 256 * 1024; // 128 Mb

    private DatagramSocket ssocket;
    private final Thread selfThread;

    public BaseUDPServer(int port) {
        selfThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                ssocket = socket;
                byte[] buffer = new byte[BUFFER_SIZE];

                log.info("started UDP server on port " + port);
                while (!ssocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    log.info("received packet from " + packet.getAddress() + ":" + packet.getPort());
                    ssocket.receive(packet);
                    handle(ssocket, packet);
                }
            } catch (IOException e) {
                log.error("failed to perform IO operation: " + e.getMessage());
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
