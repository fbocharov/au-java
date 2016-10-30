package ru.spbau.bocharov.torrent.common;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Closeable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Connection(Socket s) throws IOException {
        socket = s;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    @Override
    public void close() throws IOException {
        out.flush();
        socket.close();
    }
}
