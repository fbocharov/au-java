package ru.spbau.bocharov.ftp.client.commands;

import org.junit.Test;
import ru.spbau.bocharov.ftp.protocol.MessageType;
import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListCommandTest extends BaseCommandTest{

    @Test
    public void shouldSendRequest() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(prepareResponse()));
        ByteArrayOutputStream sockOut = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(sockOut);

        String path = "/";
        System.setOut(new PrintStream(new ByteArrayOutputStream())); // disable sout for command
        createCommand().execute(socket, path);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(result);
        stream.write(MessageType.LIST);
        stream.writeUTF(path);

        assertEquals(
                result.toString(),
                sockOut.toString());
    }

    @Test
    public void shouldListFiles() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(prepareResponse()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        System.setOut(new PrintStream(result));
        createCommand().execute(socket, "/");

        assertEquals(
                "d  foo\n" +
                "f  bar\n",
                result.toString());
    }

    @Test(expected=IOException.class)
    public void shouldThrowOnError() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        DataOutputStream stream = new DataOutputStream(tmp);
        stream.writeInt(Status.ERROR);
        stream.writeUTF("some error");
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(tmp.toByteArray()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        createCommand().execute(socket, "/");
    }

    @Test(expected=IOException.class)
    public void shouldThrowOnUnknownMessage() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        DataOutputStream stream = new DataOutputStream(tmp);
        stream.writeInt(-1);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(tmp.toByteArray()));
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        createCommand().execute(socket, "/");
    }

    @Override
    protected Command createCommand() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        return CommandFactory.getInstance().createCommand("list");
    }

    private byte[] prepareResponse() throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        DataOutputStream stream = new DataOutputStream(tmp);
        stream.writeInt(Status.SUCCESS);
        stream.writeInt(2);
        stream.writeUTF("foo"); stream.writeBoolean(true);
        stream.writeUTF("bar"); stream.writeBoolean(false);

        return tmp.toByteArray();
    }
}