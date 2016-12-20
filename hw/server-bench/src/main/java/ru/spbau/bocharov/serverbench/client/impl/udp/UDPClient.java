package ru.spbau.bocharov.serverbench.client.impl.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient extends BaseClient {

    private static final Logger log = LogManager.getLogger(UDPClient.class);
    private static final int RECV_TIMEOUT = 5 * 1000; // 5 sec

    public UDPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void run(int arraySize, int requestCount, long delta) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(serverAddress);
            socket.setSoTimeout(RECV_TIMEOUT);

            byte[] buffer = new byte[arraySize * 4 + 1024]; // array size + packet md overhead
            DatagramPacket forReceive = new DatagramPacket(buffer, buffer.length);
            while (requestCount > 0) {
                int[] before = createArray(arraySize);
                DatagramPacket packet = ProtocolIO.pack(before);
                packet.setAddress(address);
                packet.setPort(serverPort);
                socket.send(packet);
                socket.receive(forReceive);
                int[] after = ProtocolIO.unpack(forReceive);
                Arrays.sort(before);
                if (!Arrays.equals(before, after))
                    throw new RuntimeException("array is not sorted!");

                Thread.sleep(delta);
                requestCount--;
            }
        } catch (IOException e) {
            log.error("failed to perform IO: " + e.getMessage());
        } catch (InterruptedException e) {
            log.error("interrupted while sleeping between iterations: " + e.getMessage());
        }
    }
}
