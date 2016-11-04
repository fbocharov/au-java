package ru.spbau.bocharov.torrent.tracker.state;

import ru.spbau.bocharov.torrent.common.BaseStateManager;
import ru.spbau.bocharov.torrent.common.FileInfo;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StateManager extends BaseStateManager {

    private static final long MAX_SEEDER_TIMEOUT = 5 * 60 * 1000;

    private State state = new State();
    private final Object filesLock = new Object();
    private final Map<Integer, Map<InetSocketAddress, Long>> sources = new ConcurrentHashMap<>();

    public StateManager(String path, boolean loadState) {
        super(path);

        if (loadState) {
            state = loadState();
            for (FileInfo file: state.files) {
                sources.putIfAbsent(file.getFileId(), new ConcurrentHashMap<>());
            }
        }
    }

    public List<FileInfo> listFiles() {
        List<FileInfo> filesClone;
        synchronized (filesLock) {
            filesClone = new LinkedList<>(state.files);
        }
        return filesClone;
    }

    public void updateSource(int file, InetSocketAddress source) {
        sources.get(file).put(source, System.currentTimeMillis());
    }

    public int generateFileId() {
        return state.nextFileId.incrementAndGet();
    }

    public void addNewFile(int id, String name, long size) {
        FileInfo file = new FileInfo(id, name, size);
        synchronized (filesLock) {
            state.files.add(file);
            saveState(state);
        }
        sources.putIfAbsent(id, new ConcurrentHashMap<>());
    }

    public Set<InetSocketAddress> getFileSources(int fileId) {
        Set<InetSocketAddress> onlineSeeds = new HashSet<>();
        Map<InetSocketAddress, Long> fileSources = sources.get(fileId);
        if (fileSources == null) {
            // unknown file -> no sources
            return onlineSeeds;
        }
        long now = getNow();
        Iterator<Map.Entry<InetSocketAddress, Long>> it = fileSources.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<InetSocketAddress, Long> source = it.next();
            if (now - source.getValue() > MAX_SEEDER_TIMEOUT) {
                it.remove();
            } else {
                onlineSeeds.add(source.getKey());
            }
        }
        return onlineSeeds;
    }

    private static long getNow() {
        return System.currentTimeMillis();
    }

    private static final class State implements Serializable {
        AtomicInteger nextFileId = new AtomicInteger(0);
        List<FileInfo> files = new LinkedList<>();
    }
}
