package ru.spbau.bocharov.torrent.client.state;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TorrentFileTest {

    private static final long LAST_PART_SIZE = 64;
    private static final long FILE_SIZE = TorrentFile.PART_SIZE + LAST_PART_SIZE;

    @Test
    public void shouldAddAndGetParts() {
        TorrentFile file = TorrentFile.createEmpty(0, "file1.txt", FILE_SIZE);

        assertFalse(file.hasPart(0));
        assertFalse(file.isDownloaded());
        assertEquals(Collections.EMPTY_LIST, file.getParts());

        file.addPart(0);
        assertTrue(file.hasPart(0));
        assertFalse(file.isDownloaded());

        file.addPart(1);
        assertTrue(file.hasPart(1));
        assertTrue(file.isDownloaded());
        assertEquals(Arrays.asList(0, 1), file.getParts());
    }

    @Test
    public void shouldGetPartSize() {
        TorrentFile file = TorrentFile.createFull(0, "file1.txt", FILE_SIZE);
        assertEquals(TorrentFile.PART_SIZE, file.getPartSize(0));
        assertEquals(LAST_PART_SIZE, file.getPartSize(1));
    }
}