package ru.spbau.bocharov.serverbench.server.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServerStatistics {

    private static final ServerStatistics INSTANCE = new ServerStatistics();

    public static ServerStatistics getInstance() {
        return INSTANCE;
    }

    private List<Long> clientTimes = Collections.synchronizedList(new LinkedList<>());
    private List<Long> requestTimes = Collections.synchronizedList(new LinkedList<>());

    public void push(long clientProcessingTime, long requestProcessingTime) {
        clientTimes.add(clientProcessingTime);
        requestTimes.add(requestProcessingTime);
    }

    public void clear() {
        clientTimes.clear();
        requestTimes.clear();
    }

    public long getClientProcessingTime() {
        return calcAverage(clientTimes);
    }

    public long getRequestProcessingTime() {
        return calcAverage(requestTimes);
    }

    private static long calcAverage(List<Long> values) {
        int size = values.size();
        long sum = 0;

        for (int i = 0; i < size; ++i) {
            sum += values.get(i);
        }

        return sum / size;
    }

    private ServerStatistics() {}
}
