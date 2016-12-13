package ru.spbau.bocharov.serverbench.server.impl.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;

public class ThreadPerClientTCPServer extends BaseTCPServer {

    public ThreadPerClientTCPServer(int port) {
        super(port);
    }

    @Override
    protected void handle(Socket client, Job job) {
        new Thread(() -> {
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
        }).start();
    }
}
