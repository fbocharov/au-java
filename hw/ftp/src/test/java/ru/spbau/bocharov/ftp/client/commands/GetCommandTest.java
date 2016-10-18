package ru.spbau.bocharov.ftp.client.commands;

import org.junit.Test;
import ru.spbau.bocharov.ftp.protocol.MessageType;
import ru.spbau.bocharov.ftp.protocol.Status;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCommandTest extends BaseCommandTest {

    private static final String TEST_FILE_NAME = "test_file";
    private static final String TEST_FILE_CONTENT = "hello, world!";

    @Test
    public void shouldSendRequest() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(prepareResponse("")));
        ByteArrayOutputStream sockOut = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(sockOut);

        createCommand().execute(socket, TEST_FILE_NAME);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(result);
        stream.write(MessageType.GET);
        stream.writeUTF(TEST_FILE_NAME);

        assertEquals(
                result.toString(),
                sockOut.toString());
    }

    @Test
    public void shouldCreateFileWithContentReceived() throws Exception {
        Socket socket = mock(Socket.class);

        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(prepareResponse(TEST_FILE_CONTENT)));
        ByteArrayOutputStream sockOut = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(sockOut);

        createCommand().execute(socket, TEST_FILE_NAME);

        Path testFilePath = Paths.get(TEST_FILE_NAME);
        assertTrue(Files.exists(testFilePath));

        byte[] result = Files.readAllBytes(testFilePath);
        assertEquals(
                TEST_FILE_CONTENT,
                new String(result));

        Files.delete(testFilePath);
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
        return CommandFactory.getInstance().createCommand("get");
    }

    private static byte[] prepareResponse(String fileContent) throws IOException {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(response);

        stream.writeInt(Status.SUCCESS);
        long size = fileContent.length();
        stream.writeLong(size);
        stream.write(fileContent.getBytes());

        return response.toByteArray();
    }
}