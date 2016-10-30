package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ListRequest extends Request {

    @Override
    public void accept(RequestVisitor v) {
        v.visit(this);
    }

    @Override
    protected byte getType() {
        return RequestType.LIST;
    }

    @Override
    protected void serialize(DataOutputStream stream) {}

    @Override
    protected void deserialize(DataInputStream stream) {}
}
