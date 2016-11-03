package ru.spbau.bocharov.torrent.protocol;

import org.junit.Test;
import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.protocol.requests.*;
import ru.spbau.bocharov.torrent.util.BadCreatorException;
import ru.spbau.bocharov.torrent.util.ObjectFactory;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseRequestTest<T extends Request> {

    static {
        ObjectFactory<Byte, Request> factory = ObjectFactory.getInstance();
        try {
            // server requests
            factory.register(RequestType.LIST, ListRequest.class);
            factory.register(RequestType.SOURCES, SourcesRequest.class);
            factory.register(RequestType.UPDATE, UpdateRequest.class);
            factory.register(RequestType.UPLOAD, UploadRequest.class);

            // client requests
            factory.register(RequestType.STAT, StatRequest.class);
            factory.register(RequestType.GET, GetRequest.class);
        } catch (BadCreatorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSendReceive() throws IOException, BadCreatorException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(out);

        Request request = createFilledRequest();
        request.send(new Connection(socket));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        when(socket.getInputStream()).thenReturn(in);
        request = Request.receive(new Connection(socket));

        // if request is not of type T exception will be thrown and test will fail
        checkInvariants((T) request);
    }

    @Test
    public void shouldSerializeDeserialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createFilledRequest().serialize(new DataOutputStream(out));

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        T request = createEmptyRequest();
        request.deserialize(new DataInputStream(in));

        checkInvariants(request);
    }

    protected abstract T createFilledRequest();

    protected abstract T createEmptyRequest();

    protected abstract void checkInvariants(T request);
}
