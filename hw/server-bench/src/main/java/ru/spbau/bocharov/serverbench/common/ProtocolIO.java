package ru.spbau.bocharov.serverbench.common;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Arrays;

public class ProtocolIO {

    private static final int BUFFER_SIZE = 1024;

    public static void write(OutputStream stream, int[] array) throws IOException {
        Protocol.Message message = toMessage(array);
        byte[] bytes = message.toByteArray();
        DataOutputStream out = new DataOutputStream(stream);
        out.writeLong(bytes.length);
        out.write(bytes);
    }

    public static int[] read(InputStream stream) throws IOException {
        DataInputStream in = new DataInputStream(stream);

        long size = in.readLong();
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

        Protocol.Message message = Protocol.Message.parseFrom(tmp.toByteArray());

        return fromMessage(message);
    }

    public static DatagramPacket pack(int[] array) {
        Protocol.Message message = toMessage(array);
        byte[] bytes = message.toByteArray();
        return new DatagramPacket(bytes, bytes.length);
    }

    public static int[] unpack(DatagramPacket packet) throws InvalidProtocolBufferException {
        Protocol.Message message = Protocol.Message.parseFrom(packet.getData());
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
