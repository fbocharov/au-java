package ru.spbau.bocharov.serverbench.client.benchmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.client.ClientFactory;
import ru.spbau.bocharov.serverbench.common.BenchmarkResult;
import ru.spbau.bocharov.serverbench.common.ServerType;
import ru.spbau.bocharov.serverbench.util.FactoryException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BenchmarkRunner {

    private static final Logger log = LogManager.getLogger(BenchmarkRunner.class);

    private static final ClientFactory factory = ClientFactory.getInstance();

    private static final int TRY_COUNT = 10;
    private static final int SERVER_BENCHMARK_PORT = 5432;

    private final String serverAddress;
    private final int serverPort;

    public BenchmarkRunner(String address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    public BenchmarkResult run(BenchmarkConfiguration configuration) throws InterruptedException {
        BenchmarkResult result = null;
        for (int i = 0; i < TRY_COUNT; ++i) {
            try (Socket serverCtl = setupServer(configuration.getServerType())) {
                runOnce(configuration);
                result = obtainResult(serverCtl);
                // TODO: get mean from results
            } catch (IOException e) {
                log.error("failed to setup server: " + e.getMessage());
                break;
            } catch (ClassNotFoundException e) {
                log.error("server send unknown class as a result: " + e.getMessage());
                break;
            }
        }

        return result;
    }

    private void runOnce(BenchmarkConfiguration configuration) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(configuration.getClientCount());
        for (int i = 0; i < configuration.getClientCount(); ++i) {
            pool.execute(() -> {
                try {
                    BaseClient client = factory.create(configuration.getServerType(),
                            serverAddress, SERVER_BENCHMARK_PORT);
                    client.run(configuration.getArraySize(), configuration.getRequestCount(), configuration.getDelta());
                } catch (FactoryException e) {
                    log.error("failed to create client: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
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
        ObjectInputStream in = new ObjectInputStream(serverCtl.getInputStream());
        return (BenchmarkResult) in.readObject();
    }
}
