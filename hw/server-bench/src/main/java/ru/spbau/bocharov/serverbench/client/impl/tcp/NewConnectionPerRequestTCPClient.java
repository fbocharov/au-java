package ru.spbau.bocharov.serverbench.client.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class NewConnectionPerRequestTCPClient extends BaseClient {

    private static final Logger log = LogManager.getLogger(NewConnectionPerRequestTCPClient.class);

    public NewConnectionPerRequestTCPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void run(int arraySize, int requestCount, long delta) {
        while (requestCount > 0) {
            InetAddress addr = null;
            int port = -1;

            try (Socket socket = new Socket(serverAddress, serverPort);
                 InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {
//                socket.setSoTimeout(Integer.MAX_VALUE);
//                socket.setSoLinger(false, 0);
                int[] before = createArray(arraySize);
                addr = socket.getLocalAddress();
                port = socket.getLocalPort();
                ProtocolIO.write(out, before);
                log.info(addr + ":" + port + " send data");
                int[] after = ProtocolIO.read(in);
                Arrays.sort(before);
                if (!Arrays.equals(before, after)) {
                    throw new RuntimeException("array is not sorted!");
                }

                Thread.sleep(delta);
            } catch (IOException e) {
                log.error(addr + ":" + port + "failed to perform IO operation: " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                log.error("interrupted while sleeping between iterations: " + e.getMessage());
                e.printStackTrace();
            }
            requestCount--;
        }
    }
}
