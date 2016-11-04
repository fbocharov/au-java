package ru.spbau.bocharov.torrent;

import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.bocharov.torrent.client.Storage;
import ru.spbau.bocharov.torrent.client.TorrentClient;
import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.common.FileInfo;
import ru.spbau.bocharov.torrent.common.InvalidHandlerStateException;
import ru.spbau.bocharov.torrent.protocol.requests.GetRequest;
import ru.spbau.bocharov.torrent.protocol.requests.StatRequest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.spbau.bocharov.torrent.util.IOUtils.copy;

public class ClientRequestsTest extends TestWithTracker {

    private static final InetSocketAddress TRACKER_SOCK_ADDRESS =
            new InetSocketAddress(TRACKER_ADDRESS, TRACKER_PORT);

    private static final short CLIENT_PORT = 9876;
    private static final String CLIENT_STORAGE_PATH= "downloads";
    private static final List<String> CLIENT_FILES = Collections.singletonList("file.bin");
    private static final List<String> CLIENT_FILES_IN_STORAGE_PATH = new LinkedList<>();
    private static TorrentClient CLIENT;

    @Test
    public void shouldStatFile() throws IOException {
        List<FileInfo> files = listFiles();
        int file0Id = -1;
        for (FileInfo file: files) {
            if (Objects.equals(file.getFileName(), CLIENT_FILES.get(0))) {
                file0Id = file.getFileId();
            }
            assertTrue(CLIENT_FILES.contains(file.getFileName()));
        }

        List<Integer> parts = statFile(file0Id);
        assertEquals(2, parts.size());
    }

    @Test
    public void shouldGetFile() throws IOException {
        List<FileInfo> files = listFiles();
        int file0Id = -1;
        for (FileInfo file: files) {
            if (Objects.equals(file.getFileName(), CLIENT_FILES.get(0))) {
                file0Id = file.getFileId();
            }
            assertTrue(CLIENT_FILES.contains(file.getFileName()));
        }

        List<Integer> parts = statFile(file0Id);
        String outName = createFile("file0.copy.bin");
        getFile(file0Id, parts, outName);

        assertTrue(Files.equal(new File(outName), new File(CLIENT_FILES_IN_STORAGE_PATH.get(0))));
    }

    @BeforeClass
    public static void startClient() throws IOException, InvalidHandlerStateException {
        String statePath = createFile("ClientRequestsTest.state");
        CLIENT = new TorrentClient(CLIENT_PORT, TRACKER_SOCK_ADDRESS, createStorage(), statePath);
        CLIENT.start();
        for (String file: CLIENT_FILES) {
            CLIENT.upload(file);
        }
    }

    @AfterClass
    public static void stopClient() throws IOException, InterruptedException {
        if (CLIENT != null) {
            CLIENT.stop();
        }
    }


    private static List<Integer> statFile(int fileId) throws IOException {
        List<Integer> parts = new LinkedList<>();
        try (Connection connection = connectToClient()) {
            StatRequest stat = new StatRequest(fileId);
            stat.send(connection);

            DataInputStream in = connection.getIn();
            int count = in.readInt();
            for (int i = 0; i < count; ++i) {
                parts.add(in.readInt());
            }
        }
        return parts;
    }

    private static void getFile(int fileId, List<Integer> parts, String outName) throws IOException {
        try (FileOutputStream out = new FileOutputStream(outName)) {
            for (int part : parts) {
                try (Connection connection = connectToClient()) {
                    GetRequest get = new GetRequest(fileId, part);
                    get.send(connection);

                    DataInputStream in = connection.getIn();
                    long size = in.readLong();
                    copy(in, out, size);
                }
            }
        }
    }

    private static Connection connectToClient() throws IOException {
        return new Connection(new Socket("127.0.0.1", CLIENT_PORT));
    }

    private static Storage createStorage() throws IOException {
        String storagePath = createFolder(CLIENT_STORAGE_PATH);
        for (String file: CLIENT_FILES) {
            String filePath = createFile(Paths.get(CLIENT_STORAGE_PATH, file).toString());
            Path from = Paths.get(getFilePath(file));
            Path to = Paths.get(filePath);
            Files.copy(from.toFile(), to.toFile());
            CLIENT_FILES_IN_STORAGE_PATH.add(to.toString());
        }
        return new Storage(storagePath);
    }

    private static String getFilePath(String filename) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(filename);
        return resource == null ? null : resource.getFile();
    }
}
