package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.net.Socket;

class ListRequestExecutor extends BaseRequestExecutor {

    public ListRequestExecutor(Socket socket, String dir) {
        super(socket, dir);
    }

    @Override
    protected void doRun(File argument, OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(Status.SUCCESS);
        dout.writeInt(argument.listFiles().length);
        for (File file : argument.listFiles()) {
            dout.writeUTF(file.getPath());
            dout.writeBoolean(file.isDirectory());
        }
    }
}

