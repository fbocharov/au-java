package ru.spbau.bocharov.serverbench.server.impl.udp;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;
import ru.spbau.bocharov.serverbench.server.ServerException;
import ru.spbau.bocharov.serverbench.server.algo.Sort;
import ru.spbau.bocharov.serverbench.server.util.ServerStatistics;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

class Job implements Runnable {

    private static final ServerStatistics stats = ServerStatistics.getInstance();
    private static final Logger log = LogManager.getLogger(Job.class);

    private final DatagramSocket socket;
    private final DatagramPacket packet;

    Job(DatagramSocket sock, DatagramPacket p) {
        socket = sock;
        packet = p;
    }

    @Override
    public void run() {
        try {
            long requestTime = System.nanoTime();
            int[] array = ProtocolIO.unpack(packet);
            log.info("sorting " + getPacketSender() + " array");
            long clientTime = System.nanoTime();
            Sort.insertionSort(array);
            clientTime = System.nanoTime() - clientTime;

            log.info("sending response to " + getPacketSender());
            DatagramPacket response = ProtocolIO.pack(array);
            response.setAddress(packet.getAddress());
            response.setPort(packet.getPort());
            socket.send(response);
            requestTime = System.nanoTime() - requestTime;

            stats.push(clientTime, requestTime);
        } catch (InvalidProtocolBufferException e) {
            throw new ServerException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getPacketSender() {
        return String.format("%s:%d", packet.getAddress().getHostName(), packet.getPort());
    }
}
