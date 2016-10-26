package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

abstract class BaseRequestExecutor implements RequestExecutor {

    private final String baseDir;
    private final Socket socket;

    BaseRequestExecutor(Socket s, String dir) {
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
                doRun(target, out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Throwable[] suppressed = e.getSuppressed();
                throw new RuntimeException(suppressed.length == 0 ? e : suppressed[0]);
            }
        }
    }

    protected abstract void doRun(File argument, OutputStream out) throws IOException;
}
