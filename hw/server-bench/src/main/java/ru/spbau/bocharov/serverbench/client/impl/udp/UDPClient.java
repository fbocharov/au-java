package ru.spbau.bocharov.serverbench.client.impl.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.client.BaseClient;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;

import java.io.IOException;
import java.net.*;

public class UDPClient extends BaseClient {

    private static final Logger log = LogManager.getLogger(UDPClient.class);

    public UDPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void run(int arraySize, int requestCount, long delta) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(serverAddress);

            byte[] buffer = new byte[arraySize * 4 + 1024]; // array size + packet md overhead
            DatagramPacket forReceive = new DatagramPacket(buffer, buffer.length);
            while (requestCount > 0) {
                DatagramPacket packet = ProtocolIO.pack(createArray(arraySize));
                packet.setAddress(address);
                packet.setPort(serverPort);
                socket.send(packet);
                socket.receive(forReceive);
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
