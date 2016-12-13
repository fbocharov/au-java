package ru.spbau.bocharov.serverbench.server.impl.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedSizeThreadPoolUDPServer extends BaseUDPServer {

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 3;

    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    public FixedSizeThreadPoolUDPServer(short port) {
        super(port);
    }

    @Override
    protected void handle(DatagramSocket socket, DatagramPacket packet) {
        pool.execute(new Job(socket, packet));
    }
}
