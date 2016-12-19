package ru.spbau.bocharov.serverbench.client.benchmark;

import lombok.Getter;
import ru.spbau.bocharov.serverbench.common.ServerType;

public class BenchmarkConfiguration {

    @Getter
    private final int clientCount;
    @Getter
    private final ServerType serverType;
    @Getter
    private final int arraySize;
    @Getter
    private final int requestCount;
    @Getter
    private final long delta;

    public BenchmarkConfiguration(int clients, ServerType type, int arSize, int requests, long d) {
        clientCount = clients;
        serverType = type;
        arraySize = arSize;
        requestCount = requests;
        delta = d;
    }
}
