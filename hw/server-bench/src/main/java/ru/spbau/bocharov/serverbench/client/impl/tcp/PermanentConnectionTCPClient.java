package ru.spbau.bocharov.serverbench.client.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class PermanentConnectionTCPClient extends BaseClient {

    private static final Logger log = LogManager.getLogger(PermanentConnectionTCPClient.class);

    public PermanentConnectionTCPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void run(int arraySize, int requestCount, long delta) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            while (requestCount > 0) {
                int[] before = createArray(arraySize);
                ProtocolIO.write(socket.getOutputStream(), before);
                int[] after = ProtocolIO.read(socket.getInputStream());
                Arrays.sort(before);
                if (!Arrays.equals(before, after))
                    throw new RuntimeException("array is not sorted!");

                Thread.sleep(delta);
                requestCount--;
            }
        } catch (IOException e) {
            log.error("failed to perform IO operation: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("interrupted while sleeping between iterations: " + e.getMessage());
        }
    }
}
