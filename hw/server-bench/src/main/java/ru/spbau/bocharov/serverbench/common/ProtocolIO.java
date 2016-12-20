package ru.spbau.bocharov.serverbench.common;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ProtocolIO {

    private static final int BUFFER_SIZE = 1024;

    public static void write(OutputStream stream, int[] array) throws IOException {
        byte[] bytes = toRaw(array);
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    public static int[] read(InputStream stream) throws IOException {
        DataInputStream in = new DataInputStream(stream);

        int size = in.readInt();
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];

        while (size > 0) {
            int readCount = in.read(buffer, 0, BUFFER_SIZE);
            if (readCount == -1 && size > 0) {
                throw new ProtocolException("failed to read full message: stream closed");
            }

            tmp.write(buffer, 0, readCount);
            size -= readCount;
        }

        return fromRaw(tmp.toByteArray());
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
