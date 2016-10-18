package ru.spbau.bocharov.ftp.server;

import ru.spbau.bocharov.ftp.net.NetworkManager;
import ru.spbau.bocharov.ftp.server.executors.ExecutorFactory;
import ru.spbau.bocharov.ftp.server.executors.UnknownExecutorException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FTPServer {

    private final String baseDir;
    private ServerSocket serverSocket;
    private final NetworkManager networkManager;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public FTPServer(NetworkManager manager, String dir) {
        networkManager = manager;
        baseDir = dir;
    }

    public void start(int port) throws IOException {
        serverSocket = networkManager.createServerSocket(port);
        threadPool.execute(() -> {
            try {
                System.err.println("server started on port " + port);
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();
                    System.err.println("connection received from " + client.getInetAddress() + ":" + client.getPort());

                    try {
                        int type = client.getInputStream().read();
                        threadPool.execute(ExecutorFactory.getInstance().createExecutor((byte) type, client));
                    } catch (UnknownExecutorException e) {
                        System.err.println("error: " + e.getMessage());
                    }
                }
            } catch (SocketException e) {
                // server stopped -> do nothing
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stop() throws InterruptedException, IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        threadPool.shutdown();
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        serverSocket = null;
    }
}
