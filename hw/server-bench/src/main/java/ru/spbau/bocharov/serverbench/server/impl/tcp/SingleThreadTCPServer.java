package ru.spbau.bocharov.serverbench.server.impl.tcp;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class SingleThreadTCPServer extends BaseTCPServer {

    public SingleThreadTCPServer(int port) {
        super(port);
    }

    @Override
    protected void handle(Socket client, Job job) {
        try (InputStream in  = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            job.execute(in, out);
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
    }
}
