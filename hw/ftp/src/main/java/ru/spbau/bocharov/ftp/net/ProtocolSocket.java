package ru.spbau.bocharov.ftp.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ProtocolSocket {

    private final Socket socket;

    ProtocolSocket(Socket s) {
        socket = s;
    }

    public void send(byte[] bytes) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(socket.getOutputStream())) {
            stream.writeInt(bytes.length);
            stream.write(bytes);
        }
    }

    public byte[] receive() throws IOException {
        try (DataInputStream stream = new DataInputStream(socket.getInputStream())) {
            int size = stream.readInt();
            byte[] bytes = new byte[size];
            if (size != stream.read(bytes, 0, size)) {
                throw new IOException("can't read " + size + " bytes from stream");
            }

            return bytes;
        }
    }
}
