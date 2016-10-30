package ru.spbau.bocharov.torrent.protocol.requests;

import lombok.Getter;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UploadRequest extends Request {

    @Getter
    private String fileName;
    @Getter
    private long size;

    public UploadRequest() {}

    public UploadRequest(String file, long sz) {
        fileName = file;
        size = sz;
    }

    @Override
    public void accept(RequestVisitor v) {
        v.visit(this);
    }

    @Override
    protected byte getType() {
        return RequestType.UPLOAD;
    }

    @Override
    protected void serialize(DataOutputStream stream) throws IOException {
        stream.writeUTF(fileName);
        stream.writeLong(size);
    }

    @Override
    protected void deserialize(DataInputStream stream) throws IOException {
        fileName = stream.readUTF();
        size = stream.readLong();
    }
}
