package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.Status;
import ru.spbau.bocharov.ftp.utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

public class GetRequestExecutor implements RequestExecutor {

    private final String baseDir;
    private final Socket socket;

    public GetRequestExecutor(Socket s, String dir) {
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
            if (!target.exists() || !target.isFile()) {
                out.writeInt(Status.ERROR);
                out.writeUTF(path + ": no such file");
            } else {
                out.writeInt(Status.SUCCESS);
                long size = target.length();
                out.writeLong(size);
                try (FileInputStream file = new FileInputStream(target)) {
                    IOUtils.copy(file, out, size);
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
