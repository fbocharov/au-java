package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

public class ListRequestExecutor implements RequestExecutor {

    private final String baseDir;
    private final Socket socket;

    public ListRequestExecutor(Socket s, String dir) {
        socket = s;
        baseDir = dir;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String path = in.readUTF();

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            File target = Paths.get(baseDir, path).toFile();
            if (!target.exists() || !target.isDirectory()) {
                out.writeInt(Status.ERROR);
                out.writeUTF(path + ": no such directory");
            } else {
                out.writeInt(Status.SUCCESS);
                out.writeInt(target.listFiles().length);
                for (File file : target.listFiles()) {
                    out.writeUTF(file.getPath());
                    out.writeBoolean(file.isDirectory());
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

