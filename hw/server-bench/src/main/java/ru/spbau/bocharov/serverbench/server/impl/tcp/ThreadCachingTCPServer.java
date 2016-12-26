package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadCachingTCPServer extends BaseTCPServer {

    private static final Logger log = LogManager.getLogger(ThreadCachingTCPServer.class);

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ThreadCachingTCPServer(int port) {
        super(port);
    }

    @Override
    protected void shutdownServer() throws IOException, InterruptedException {
        super.shutdownServer();
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    @Override
    protected void handle(Socket client, Job job) {
        pool.execute(() -> {
            try {
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                log.info("start handling " + client.getInetAddress().getHostName() + ":" + client.getPort());
                while (!client.isClosed()) {
                    job.execute(in, out);
                }
                log.info("done handling " + client.getInetAddress().getHostName() + ":" + client.getPort());
            } catch (IOException e) {
                log.error("io error occured: " + e.getMessage());
                throw new UncheckedIOException(e);
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    log.error("bad things happend: " + e.getMessage());
                    IOException all = new IOException(e);
                    Arrays.stream(e.getSuppressed()).forEach(all::addSuppressed);
                    throw new UncheckedIOException(all);
                }
            }
        });
    }
}
