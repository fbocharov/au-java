package ru.spbau.bocharov.serverbench.client;

import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkConfiguration;
import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkRunner;
import ru.spbau.bocharov.serverbench.common.BenchmarkResult;
import ru.spbau.bocharov.serverbench.common.ServerType;

public class ConsoleClientMain {

    public static void main(String[] args) throws InterruptedException {

        String serverAddress = args[0];
        int serverPort = Integer.valueOf(args[1]);

        int serverType = Integer.valueOf(args[2]);
        int clientCount = Integer.valueOf(args[3]);
        int arraySize = Integer.valueOf(args[4]);
        int requestCount = Integer.valueOf(args[5]);
        long delta = Long.valueOf(args[6]);

        BenchmarkRunner runner = new BenchmarkRunner(serverAddress, serverPort);
        BenchmarkConfiguration configuration = new BenchmarkConfiguration(clientCount,
                ServerType.values()[serverType], arraySize, requestCount, delta);
        BenchmarkResult result = runner.run(configuration);
        // TODO: print result
    }
}
