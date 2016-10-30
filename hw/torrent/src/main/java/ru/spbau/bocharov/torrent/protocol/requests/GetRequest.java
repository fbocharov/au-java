package ru.spbau.bocharov.torrent.protocol.requests;

import lombok.Getter;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetRequest extends Request {

    @Getter
    private int fileId = -1;
    @Getter
    private int partId = -1;

    public GetRequest() {}

    public GetRequest(int file, int part) {
        fileId = file;
        partId = part;
    }

    @Override
    public void accept(RequestVisitor v) {
        v.visit(this);
    }

    @Override
    protected byte getType() {
        return RequestType.GET;
    }

    @Override
    protected void serialize(DataOutputStream stream) throws IOException {
        stream.writeInt(fileId);
        stream.writeInt(partId);
    }

    @Override
    protected void deserialize(DataInputStream stream) throws IOException {
        fileId = stream.readInt();
        partId = stream.readInt();
    }
}
