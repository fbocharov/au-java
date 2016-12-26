package ru.spbau.bocharov.serverbench.client.benchmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.client.ClientFactory;
import ru.spbau.bocharov.serverbench.common.ServerType;
import ru.spbau.bocharov.serverbench.util.FactoryException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BenchmarkRunner {

    private static final Logger log = LogManager.getLogger(BenchmarkRunner.class);

    private static final ClientFactory factory = ClientFactory.getInstance();

    private static final int TRY_COUNT = 5;
    private static final int SERVER_BENCHMARK_PORT = 5432;

    private final String serverAddress;
    private final int serverPort;

    private final AtomicLong clientRunningTime = new AtomicLong(0);

    public BenchmarkRunner(String address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    public BenchmarkResult run(BenchmarkConfiguration configuration) throws InterruptedException {
        long clientRunTime = 0;
        long clientProcTime = 0;
        long requestProcTime = 0;
        for (int i = 0; i < TRY_COUNT; ++i) {
            try (Socket serverCtl = setupServer(configuration.getServerType())) {
                runOnce(configuration);
                BenchmarkResult result = obtainResult(serverCtl);
                clientRunTime += result.clientRunningTime;
                clientProcTime += result.clientProcessingTime;
                requestProcTime += result.requestProcessingTime;
            } catch (IOException e) {
                log.error("io error occured: " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                log.error("server send unknown class as a result: " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (BrokenBarrierException e) {
                log.error("failed to await barrier: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }

        return new BenchmarkResult(
                clientRunTime / TRY_COUNT,
                clientProcTime / TRY_COUNT,
                requestProcTime / TRY_COUNT);
    }

    private void runOnce(BenchmarkConfiguration configuration) throws InterruptedException, BrokenBarrierException {
        clientRunningTime.set(0);
        ExecutorService pool = Executors.newFixedThreadPool(configuration.getClientCount());
        CyclicBarrier barrier = new CyclicBarrier(configuration.getClientCount() + 1);
        for (int i = 0; i < configuration.getClientCount(); ++i) {
            pool.execute(() -> {
                try {
                    BaseClient client = factory.create(configuration.getServerType(),
                            serverAddress, SERVER_BENCHMARK_PORT);
                    barrier.await();
                    long time = System.nanoTime();
                    client.run(configuration.getArraySize(), configuration.getRequestCount(), configuration.getDelta());
                    time = System.nanoTime() - time;
                    clientRunningTime.addAndGet(time / configuration.getClientCount());
                } catch (FactoryException e) {
                    log.error("failed to create client: " + e.getMessage());
                    throw new RuntimeException(e);
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.error("failed to await barrier: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
        barrier.await();
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private Socket setupServer(ServerType type) throws IOException {
        Socket serverCtl = new Socket(serverAddress, serverPort);

        DataOutputStream out = new DataOutputStream(serverCtl.getOutputStream());
        out.writeUTF(type.toString());
        out.writeInt(SERVER_BENCHMARK_PORT);

        DataInputStream in = new DataInputStream(serverCtl.getInputStream());
        String response = in.readUTF();
        if (!Objects.equals(response, "OK")) {
            serverCtl.close();
            throw new IOException(response);
        }

        return serverCtl;
    }

    private BenchmarkResult obtainResult(Socket serverCtl) throws IOException, ClassNotFoundException {
        new DataOutputStream(serverCtl.getOutputStream()).writeInt(0);
        DataInputStream in = new DataInputStream(serverCtl.getInputStream());
        long clientProcessingTime = in.readLong();
        long requestProcessingTime = in.readLong();

        return new BenchmarkResult(clientRunningTime.get(), clientProcessingTime, requestProcessingTime);
    }
}
