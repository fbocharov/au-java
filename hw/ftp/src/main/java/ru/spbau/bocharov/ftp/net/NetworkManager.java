package ru.spbau.bocharov.ftp.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

    public ProtocolSocket createSocket(InetAddress address, int port) throws IOException {
        return new ProtocolSocket(new Socket(address, port));
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }


}
