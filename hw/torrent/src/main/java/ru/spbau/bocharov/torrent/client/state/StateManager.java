package ru.spbau.bocharov.torrent.client.state;

import ru.spbau.bocharov.torrent.common.BaseStateManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateManager extends BaseStateManager {

    private Map<Integer, TorrentFile> files = new ConcurrentHashMap<>();

    public StateManager(String path, boolean loadState) {
        super(path);

        if (loadState) {
            files = loadState();
        }
    }

    public TorrentFile getFile(int id) {
        return files.get(id);
    }

    public Iterable<TorrentFile> listFiles() {
        return files.values();
    }

    public void addNewFile(TorrentFile file) {
        files.put(file.getFileId(), file);
        saveState(files);
    }

    public void addPart(int fileId, int partId) {
        TorrentFile file = files.get(fileId);
        file.addPart(partId);
        saveState(files);
    }
}
