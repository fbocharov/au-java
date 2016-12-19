package ru.spbau.bocharov.serverbench.server.impl.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ThreadPerClientUDPServer extends BaseUDPServer {

    public ThreadPerClientUDPServer(int port) {
        super(port);
    }

    @Override
    protected void handle(DatagramSocket socket, DatagramPacket packet) {
        new Thread(new Job(socket, packet)).start();
    }
}
