package ru.spbau.bocharov.torrent;

import org.junit.*;
import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.common.FileInfo;
import ru.spbau.bocharov.torrent.common.InvalidHandlerStateException;
import ru.spbau.bocharov.torrent.protocol.requests.ListRequest;
import ru.spbau.bocharov.torrent.protocol.requests.SourcesRequest;
import ru.spbau.bocharov.torrent.protocol.requests.UpdateRequest;
import ru.spbau.bocharov.torrent.protocol.requests.UploadRequest;
import ru.spbau.bocharov.torrent.tracker.TorrentTracker;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class TestWithTracker extends TestWithTempFolder {

    static final String TRACKER_ADDRESS = "127.0.0.1";
    static final short TRACKER_PORT = TorrentTracker.DEFAULT_PORT;
    private static TorrentTracker TRACKER;

    @BeforeClass
    public static void startTracker() throws IOException, InvalidHandlerStateException {
        String trackerPath = createFile("tracker.state");
        TRACKER = new TorrentTracker(trackerPath);
        TRACKER.start();
    }

    @AfterClass
    public static void stopTracker() throws IOException, InterruptedException {
        if (TRACKER != null) {
            TRACKER.stop();
        }
    }


    static int uploadFile(String name, long size) throws IOException {
        try (Connection connection = connectToTracker()) {
            UploadRequest upload = new UploadRequest(name, size);
            upload.send(connection);

            return connection.getIn().readInt();
        }
    }

    static List<FileInfo> listFiles() throws IOException {
        List<FileInfo> files = new LinkedList<>();
        try (Connection connection = connectToTracker()) {
            ListRequest list = new ListRequest();
            list.send(connection);

            DataInputStream in = connection.getIn();
            int count = in.readInt();
            for (int i = 0; i < count; ++i) {
                FileInfo file = new FileInfo();
                file.deserialize(in);
                files.add(file);
            }
        }
        return files;
    }

    static List<InetSocketAddress> getSources(int fileId) throws IOException {
        List<InetSocketAddress> sources = new LinkedList<>();

        try (Connection connection = connectToTracker()) {
            SourcesRequest request = new SourcesRequest(fileId);
            request.send(connection);

            DataInputStream in = connection.getIn();
            int count = in.readInt();
            for (int i = 0; i < count; ++i) {
                String address = in.readUTF();
                short port = in.readShort();
                sources.add(new InetSocketAddress(address, port));
            }
        }

        return sources;
    }

    static void updateFiles(List<Integer> files, short port) throws IOException {
        try (Connection connection = connectToTracker()) {
            UpdateRequest request = new UpdateRequest(port, files);
            request.send(connection);

            if (!connection.getIn().readBoolean()) {
                System.err.println("failed to receive update ack from tracker");
            }
        }
    }

    private static Connection connectToTracker() throws IOException {
        return new Connection(new Socket(TRACKER_ADDRESS, TRACKER_PORT));
    }
}
