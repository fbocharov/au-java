package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.Status;
import ru.spbau.bocharov.ftp.utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

class GetRequestExecutor extends BaseRequestExecutor {

    public GetRequestExecutor(Socket socket, String dir) {
        super(socket, dir);
    }

    @Override
    protected void doRun(File argument, OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(Status.SUCCESS);
        long size = argument.length();
        dout.writeLong(size);
        try (FileInputStream file = new FileInputStream(argument)) {
            IOUtils.copy(file, out, size);
        }
    }
}
