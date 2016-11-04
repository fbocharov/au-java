package ru.spbau.bocharov.torrent;

import org.junit.Test;
import ru.spbau.bocharov.torrent.common.FileInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackerRequestsTest extends TestWithTracker {

    private static final String FILE_NAME1 = "file1.txt";
    private static final long FILE_SIZE1 = 1234;
    private static final String FILE_NAME2 = "file2.txt";
    private static final long FILE_SIZE2 = 2345;
    private static final String FILE_NAME3 = "file3.txt";
    private static final long FILE_SIZE3 = 3456;

    private static final int UNKNOWN_FILE_ID = 100500;

    private static final short CLIENT_PORT = 9876;

    @Test
    public void shouldUploadAndListFile() throws IOException {
        int fileId1 = uploadFile(FILE_NAME1, FILE_SIZE1);
        FileInfo file1 = new FileInfo(fileId1, FILE_NAME1, FILE_SIZE1);
        int fileId2 = uploadFile(FILE_NAME2, FILE_SIZE2);
        FileInfo file2 = new FileInfo(fileId2, FILE_NAME2, FILE_SIZE2);

        List<FileInfo> files = listFiles();

        assertThat(files, containsInAnyOrder(file1, file2));
    }

    @Test
    public void shouldUploadUpdateAndGetSources() throws IOException {
        int fileId = uploadFile(FILE_NAME3, FILE_SIZE3);
        updateFiles(Collections.singletonList(fileId), CLIENT_PORT);

        assertEquals(
                Collections.singletonList(new InetSocketAddress("127.0.0.1", CLIENT_PORT)),
                getSources(fileId));
    }

    @Test
    public void shouldGetNoSourcesOnUnknownFile() throws IOException {
        assertTrue(getSources(UNKNOWN_FILE_ID).isEmpty());
    }
}
