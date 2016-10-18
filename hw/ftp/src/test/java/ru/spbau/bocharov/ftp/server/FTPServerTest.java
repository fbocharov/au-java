package ru.spbau.bocharov.ftp.server;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.bocharov.ftp.net.NetworkManager;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class FTPServerTest {

    private static final int TEST_PORT = 11111;
    @Rule
    public TemporaryFolder serverFolder = new TemporaryFolder();

    @Test
    public void shouldCreateThreadsOnStart() throws IOException, InterruptedException {
        FTPServer server = createServer();

        int threadsBefore = Thread.currentThread().getThreadGroup().activeCount();
        server.start(TEST_PORT);

        assertEquals(
                threadsBefore + 1,
                Thread.currentThread().getThreadGroup().activeCount());

        server.stop();
    }

    private FTPServer createServer() {
        return new FTPServer(new NetworkManager(), serverFolder.getRoot().toString());
    }
}