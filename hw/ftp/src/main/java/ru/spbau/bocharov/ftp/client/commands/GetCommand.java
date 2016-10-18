package ru.spbau.bocharov.ftp.client.commands;

import ru.spbau.bocharov.ftp.protocol.MessageType;
import ru.spbau.bocharov.ftp.utils.IOUtils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GetCommand extends FTPCommand {

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
        return MessageType.GET;
    }

    @Override
    protected void onSuccess(DataInputStream stream) throws IOException {
        long size = stream.readLong();
        if (size > 0) {
            try (FileOutputStream file = new FileOutputStream(path)) {
                IOUtils.copy(stream, file, size);
            }
        }
    }

    @Override
    protected void onError(DataInputStream stream) throws IOException {
        throw new IOException("remote error: " + stream.readUTF());
    }
}
