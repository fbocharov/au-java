package ru.spbau.bocharov.serverbench.server.impl.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CachedThreadPoolTCPServer extends BaseTCPServer {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public CachedThreadPoolTCPServer(int port) {
        super(port);
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    @Override
    protected void handle(Socket client, Job job) {
        pool.execute(() -> {
            try (InputStream in = client.getInputStream();
                 OutputStream out = client.getOutputStream()) {
                while (!client.isClosed()) {
                    job.execute(in, out);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    IOException all = new IOException(e);
                    Arrays.stream(e.getSuppressed()).forEach(all::addSuppressed);
                    throw new UncheckedIOException(all);
                }
            }
        });
    }
}
