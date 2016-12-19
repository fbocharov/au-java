package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class SingleThreadTCPServer extends BaseTCPServer {

    private static final Logger log = LogManager.getLogger(SingleThreadTCPServer.class);

    public SingleThreadTCPServer(int port) {
        super(port);
    }

    @Override
    protected void handle(Socket client, Job job) {
        try (InputStream in  = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            log.info("start handling " + client.getInetAddress().getHostName() + ":" + client.getPort());
            job.execute(in, out);
            log.info("done handling " + client.getInetAddress().getHostName() + ":" + client.getPort());
        } catch (IOException e) {
            log.error("io error occured: " + e.getMessage());
            throw new UncheckedIOException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("bad things had happend: " + e.getMessage());
                IOException all = new IOException(e);
                Arrays.stream(e.getSuppressed()).forEach(all::addSuppressed);
                throw new UncheckedIOException(all);
            }
        }
    }
}
