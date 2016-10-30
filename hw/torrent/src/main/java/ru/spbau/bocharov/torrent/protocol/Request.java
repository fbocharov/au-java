package ru.spbau.bocharov.torrent.protocol;

import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.util.BadCreatorException;
import ru.spbau.bocharov.torrent.util.ObjectFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Request {

    public void send(Connection connection) throws IOException {
        DataOutputStream out = connection.getOut();
        out.writeByte(getType());
        serialize(out);
    }

    public static Request receive(Connection connection) throws IOException, BadCreatorException {
        DataInputStream in = connection.getIn();
        ObjectFactory<Byte, Request> factory = ObjectFactory.<Byte, Request>getInstance();

        byte type = in.readByte();
        Request request = factory.create(type);
        request.deserialize(in);

        return request;
    }

    public abstract void accept(RequestVisitor v);

    protected abstract byte getType();

    protected abstract void serialize(DataOutputStream stream) throws IOException;

    protected abstract void deserialize(DataInputStream stream) throws IOException;
}
