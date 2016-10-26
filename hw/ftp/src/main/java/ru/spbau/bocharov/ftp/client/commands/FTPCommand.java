package ru.spbau.bocharov.ftp.client.commands;

import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.net.Socket;
import java.util.List;

public abstract class FTPCommand implements Command {

    @Override
    public void execute(Socket socket, String... args) throws IOException {
        setArguments(args);

        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(getMessageType());
        for (String arg: getArguments()) {
            outputStream.writeUTF(arg);
        }

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        int status = inputStream.readInt();
        switch (status) {
            case Status.SUCCESS:
                onSuccess(inputStream);
                break;
            case Status.ERROR:
                onError(inputStream);
                break;
            default:
                throw new IOException("unknown request status: " + status);
        }
    }

    protected abstract void setArguments(String... args) throws IOException;
    protected abstract List<String> getArguments();

    protected abstract int getMessageType();

    protected abstract void onSuccess(DataInputStream stream) throws IOException;

    protected abstract void onError(DataInputStream stream) throws IOException;
}
