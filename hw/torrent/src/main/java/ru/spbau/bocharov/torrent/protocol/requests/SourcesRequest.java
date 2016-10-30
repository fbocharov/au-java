package ru.spbau.bocharov.torrent.protocol.requests;


import lombok.Getter;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SourcesRequest extends Request {

    @Getter
    private int fileId = -1;

    public SourcesRequest() {}

    public SourcesRequest(int file) {
        fileId = file;
    }

    @Override
    public void accept(RequestVisitor v) {
        v.visit(this);
    }

    @Override
    protected byte getType() {
        return RequestType.SOURCES;
    }

    @Override
    protected void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(fileId);
    }

    @Override
    protected void deserialize(DataInputStream stream) throws IOException {
        fileId = stream.readInt();
    }
}
