package ru.spbau.bocharov.serverbench.common;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ProtocolIO {

    public static void write(OutputStream stream, int[] array) throws IOException {
        byte[] bytes = toRaw(array);
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(bytes.length);
        stream.write(bytes);
    }

    public static int[] read(InputStream stream) throws IOException {
        final int messageLength = new DataInputStream(stream).readInt();
        byte[] data = new byte[messageLength];
        final int readCount = IOUtils.read(stream, data);
        assert readCount == messageLength;

        return fromRaw(data);
    }

    public static DatagramPacket pack(int[] array) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        Protocol.Message message = toMessage(array);
        byte[] msgBytes = message.toByteArray();
        out.writeInt(msgBytes.length);
        out.write(msgBytes);

        byte[] bytes = byteStream.toByteArray();
        return new DatagramPacket(bytes, bytes.length);
    }

    public static byte[] toRaw(int[] array) {
        Protocol.Message message = toMessage(array);
        return message.toByteArray();
    }

    public static int[] fromRaw(byte[] bytes) throws InvalidProtocolBufferException {
        Protocol.Message message = Protocol.Message.parseFrom(bytes);
        return fromMessage(message);
    }

    public static int[] unpack(DatagramPacket packet) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(packet.getData());
        DataInputStream in = new DataInputStream(byteStream);

        int size = in.readInt();
        byte[] msgBytes = new byte[size];
        if (size != in.read(msgBytes)) {
            throw new IOException("can't read message bytes from packet");
        }

        Protocol.Message message = Protocol.Message.parseFrom(msgBytes);
        return fromMessage(message);
    }

    private static int[] fromMessage(Protocol.Message message) {
        int[] array = new int[message.getValueCount()];
        for (int i = 0; i < message.getValueCount(); ++i) {
            array[i] = message.getValue(i);
        }

        return array;
    }

    private static Protocol.Message toMessage(int[] array) {
        Protocol.Message.Builder builder = Protocol.Message.newBuilder();
        Arrays.stream(array).forEach(builder::addValue);
        return builder.build();
    }
}
