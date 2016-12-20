package ru.spbau.bocharov.serverbench.client.benchmark;

import java.io.Serializable;

public class BenchmarkResult implements Serializable {

    public final long clientRunningTime;
    public final long clientProcessingTime;
    public final long requestProcessingTime;

    public BenchmarkResult(long clientRunTime, long clientProcTime, long reqProcTime) {
        clientRunningTime = clientRunTime;
        clientProcessingTime = clientProcTime;
        requestProcessingTime = reqProcTime;
    }
}
