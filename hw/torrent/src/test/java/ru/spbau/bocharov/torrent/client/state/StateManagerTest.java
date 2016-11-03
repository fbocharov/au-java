package ru.spbau.bocharov.torrent.client.state;

import org.junit.Test;
import ru.spbau.bocharov.torrent.TestWithTempFolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StateManagerTest extends TestWithTempFolder {

    @Test
    public void shouldAddAndGetFiles() {
        String statePath = createFile("shouldAddAndGetFiles.state");
        StateManager manager = new StateManager(statePath, false);

        TorrentFile file = TorrentFile.createEmpty(0, "file1.txt", 12345);
        manager.addNewFile(file);

        TorrentFile managerFile = manager.getFile(0);
        assertEquals(file.getFileName(), managerFile.getFileName());
        assertEquals(file.getFileId(), managerFile.getFileId());
        assertEquals(file.getParts(), managerFile.getParts());
    }

    @Test
    public void shouldModifyFileThroughManager() {
        String statePath = createFile("shouldModifyFileThroughManager.state");
        StateManager manager = new StateManager(statePath, false);

        TorrentFile file = TorrentFile.createEmpty(0, "file1.txt", 12345);
        manager.addNewFile(file);

        manager.addPart(0, 0);
        assertTrue(manager.getFile(0).hasPart(0));
    }

    @Test
    public void shouldListExistingFiles() {
        String statePath = createFile("shouldListExistingFiles.state");
        StateManager manager = new StateManager(statePath, false);

        List<TorrentFile> files = Arrays.asList(
                TorrentFile.createEmpty(0, "file0.txt", 1234),
                TorrentFile.createFull(1, "file1.txt", 2345));

        for (TorrentFile file: files) {
            manager.addNewFile(file);
        }

        assertTrue(Arrays.equals(
                files.toArray(),
                manager.listFiles().toArray()));
    }
}