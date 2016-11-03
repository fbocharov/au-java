package ru.spbau.bocharov.torrent.tracker.state;

import org.junit.Test;
import ru.spbau.bocharov.torrent.TestWithTempFolder;
import ru.spbau.bocharov.torrent.common.FileInfo;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class StateManagerTest extends TestWithTempFolder {

    @Test
    public void shouldAddAndListFiles() {
        String statePath = createFile("shouldAddAndListFiles.state");
        StateManager manager = new StateManager(statePath, false);

        List<FileInfo> files = Arrays.asList(
                new FileInfo(manager.generateFileId(), "file0.txt", 1234),
                new FileInfo(manager.generateFileId(), "file1.txt", 2345));
        for (FileInfo file: files) {
            manager.addNewFile(file.getFileId(), file.getFileName(), file.getSize());
        }

        assertEquals(files, manager.listFiles());
    }

    @Test
    public void shouldUpdateAndGetSources() {
        String statePath = createFile("shouldUpdateAndGetSources.state");
        StateManager manager = new StateManager(statePath, false);

        List<InetSocketAddress> sources = Arrays.asList(
                new InetSocketAddress("127.0.0.1", 8888),
                new InetSocketAddress("127.0.0.1", 9999),
                new InetSocketAddress("127.0.0.1", 11111));
        for (int i = 0; i < sources.size(); ++i) {
            manager.addNewFile(i, "file" + i, i);
            manager.updateSource(i, sources.get(i));
        }

        for (int i = 0; i < sources.size(); ++i) {
            assertEquals(
                    Collections.singleton(sources.get(i)),
                    manager.getFileSources(i));
        }
    }
}