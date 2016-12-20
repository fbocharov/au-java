package ru.spbau.bocharov.serverbench.client;

import java.util.Random;

public abstract class BaseClient {

    protected final String serverAddress;
    protected final int serverPort;

    public BaseClient(String address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    public abstract void run(int arraySize, int requestCount, long delta);

    protected static int[] createArray(int size) {
        int[] array = new int[size];
        Random random = new Random(228);
        for (int i = 0; i < size; ++i) {
            array[i] = random.nextInt();
        }
        return array;
    }

}
