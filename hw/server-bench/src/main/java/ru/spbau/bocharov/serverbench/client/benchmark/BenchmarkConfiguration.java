package ru.spbau.bocharov.serverbench.client.benchmark;

import lombok.Getter;
import ru.spbau.bocharov.serverbench.common.ServerType;

import java.util.Iterator;

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

    public static Iterator<BenchmarkConfiguration> makeIterator(
            ServerType serverType, ParameterRange arraySizeRange, ParameterRange clientsRange,
            ParameterRange requestCountRange, ParameterRange deltaRange) {
        return new Iterator<BenchmarkConfiguration>() {

            private int arraySize = arraySizeRange.from;
            private int clientCount = clientsRange.from;
            private int requestCount = requestCountRange.from;
            private int delta = deltaRange.from;

            @Override
            public boolean hasNext() {
                return arraySize < arraySizeRange.to &&
                        clientCount < clientsRange.to &&
                        requestCount < requestCountRange.to &&
                        delta < deltaRange.to;
            }

            @Override
            public BenchmarkConfiguration next() {
                if (!hasNext()) {
                    return null;
                }

                BenchmarkConfiguration configuration = new BenchmarkConfiguration(
                        clientCount, serverType, arraySize, requestCount, delta);

                delta += deltaRange.step;

                if (delta >= deltaRange.to) {
                    delta = deltaRange.from;
                    requestCount += requestCountRange.step;
                }

                if (requestCount >= requestCountRange.to) {
                    requestCount = requestCountRange.from;
                    clientCount += clientsRange.step;
                }

                if (clientCount >= clientsRange.to) {
                    clientCount = clientsRange.from;
                    arraySize += arraySizeRange.step;
                }

                return configuration;
            }
        };
    }
}
