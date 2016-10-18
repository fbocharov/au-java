package ru.spbau.bocharov.ftp.client.commands;

import ru.spbau.bocharov.ftp.protocol.MessageType;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListCommand extends FTPCommand {

    private String path;

    @Override
    protected void setArguments(String... args) throws IOException {
        if (args == null || args.length != 1) {
            throw new IOException("you should specify path");
        }

        path = args[0];
    }

    @Override
    protected List<String> getArguments() {
        return Collections.singletonList(path);
    }

    @Override
    protected int getMessageType() {
        return MessageType.LIST;
    }

    @Override
    protected void onSuccess(DataInputStream stream) throws IOException {
        int fileCount = stream.readInt();
        for (int i = 0; i < fileCount; ++i) {
            String path = stream.readUTF();
            boolean isDir = stream.readBoolean();
            System.out.format("%c  %s\n", isDir ? 'd' : 'f', path);
        }
    }

    @Override
    protected void onError(DataInputStream stream) throws IOException {
        throw new IOException("remote error: " + stream.readUTF());
    }
}
