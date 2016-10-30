package ru.spbau.bocharov.torrent.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestVisitor;
import ru.spbau.bocharov.torrent.util.BadCreatorException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ConnectionHandler {

    private static final Logger log = LogManager.getLogger(ConnectionHandler.class);

    private final short port;
    private ServerSocket socket = null;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public ConnectionHandler(short p) {
        port = p;
    }

    public void start() throws IOException, InvalidHandlerStateException {
        if (socket != null) {
            throw new InvalidHandlerStateException("server already started");
        }

        socket = new ServerSocket(port);

        pool.execute(() -> {
            try {
                log.info("start server on port {}", port);
                while (!socket.isClosed()) {
                    Connection connection = new Connection(socket.accept());
                    log.info("connection received, handling...");
                    pool.execute(() -> handle(connection));
                }
            } catch (SocketException ignored) {
                // socket closed -> do nothing
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public void stop() throws IOException, InterruptedException {
        if (socket != null) {
            socket.close();
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        socket = null;
    }


    protected abstract class BaseResponser implements RequestVisitor {

        protected final Connection connection;

        protected BaseResponser(Connection conn) {
            connection = conn;
        }

        protected void answerUnsupportedRequestType() {
            DataOutputStream out = connection.getOut();
            try {
                out.writeInt(-1);
                out.writeUTF("unsupported request type");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    protected abstract BaseResponser createResponser(Connection connection);

    private void handle(Connection connection) {
        try {
            Request request = Request.receive(connection);
            request.accept(createResponser(connection));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (BadCreatorException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        log.info("connection handling complete");
    }
}
