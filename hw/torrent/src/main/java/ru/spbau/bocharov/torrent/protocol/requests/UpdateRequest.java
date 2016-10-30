package ru.spbau.bocharov.torrent.protocol.requests;

import lombok.Getter;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class UpdateRequest extends Request {

    @Getter
    private short clientPort = -1;
    @Getter
    private List<Integer> fileIds = new LinkedList<>();

    public UpdateRequest() {}

    public UpdateRequest(short port, List<Integer> files) {
        clientPort = port;
        fileIds = new LinkedList<>(files);
    }

    @Override
    public void accept(RequestVisitor v) {
        v.visit(this);
    }

    @Override
    protected byte getType() {
        return RequestType.UPDATE;
    }

    @Override
    protected void serialize(DataOutputStream stream) throws IOException {
        stream.writeShort(clientPort);
        stream.writeInt(fileIds.size());
        for (int id: fileIds) {
            stream.writeInt(id);
        }
    }

    @Override
    protected void deserialize(DataInputStream stream) throws IOException {
        clientPort = stream.readShort();
        int count = stream.readInt();
        for (int i = 0; i < count; ++i) {
            fileIds.add(stream.readInt());
        }
    }
}
