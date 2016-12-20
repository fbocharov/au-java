package ru.spbau.bocharov.serverbench.server.impl.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.server.BaseServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class BaseUDPServer extends BaseServer {

    private static final Logger log = LogManager.getLogger(BaseUDPServer.class);

    private static final int BUFFER_SIZE = 256 * 1024; // 256 Kb

    private DatagramSocket ssocket;

    public BaseUDPServer(int port) {
        super(port);
    }

    @Override
    protected Runnable createServer(int port) {
        return () -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                ssocket = socket;
                byte[] buffer = new byte[BUFFER_SIZE];

                log.info("started UDP server on port " + port);
                while (!ssocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    try {
                        ssocket.receive(packet);
                    } catch (IOException e) {
                        log.warn("IO error on receive, possibly stopping server: " + e.getMessage());
                        continue;
                    }
                    log.info("received packet from " + packet.getAddress() + ":" + packet.getPort());
                    handle(ssocket, packet);
                }
            } catch (IOException e) {
                log.error("IO error occured: " + e.getMessage());
                throw new UncheckedIOException(e);
            }
        };
    }

    @Override
    protected void shutdownServer() {
        ssocket.close();
    }

    protected abstract void handle(DatagramSocket socket, DatagramPacket packet);
}
