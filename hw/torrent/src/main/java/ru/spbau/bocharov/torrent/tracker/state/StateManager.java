package ru.spbau.bocharov.torrent.tracker.state;

import ru.spbau.bocharov.torrent.common.BaseStateManager;
import ru.spbau.bocharov.torrent.common.FileInfo;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StateManager extends BaseStateManager {

    private static final long MAX_SEEDER_TIMEOUT = 5 * 60 * 1000;

    private AtomicInteger nextFileId = new AtomicInteger(0);
    private List<FileInfo> files = new LinkedList<>();
    private final Object filesLock = new Object();
    private Map<Integer, Map<InetSocketAddress, Long>> sources = new ConcurrentHashMap<>();

    public StateManager(String path, boolean loadState) {
        super(path);

        if (loadState) {
            files = loadState();
            for (FileInfo file: files) {
                sources.putIfAbsent(file.getFileId(), new ConcurrentHashMap<>());
            }
        }
    }

    public List<FileInfo> listFiles() {
        List<FileInfo> filesClone;
        synchronized (filesLock) {
            filesClone = new LinkedList<>(files);
        }
        return filesClone;
    }

    public void updateSource(int file, InetSocketAddress source) {
        sources.get(file).put(source, System.currentTimeMillis());
    }

    public int generateFileId() {
        return nextFileId.incrementAndGet();
    }

    public void addNewFile(int id, String name, long size) {
        FileInfo file = new FileInfo(id, name, size);
        synchronized (filesLock) {
            files.add(file);
            saveState(files);
        }
        sources.putIfAbsent(id, new ConcurrentHashMap<>());
    }

    public Set<InetSocketAddress> getFileSources(int fileId) {
        Set<InetSocketAddress> seeders = new HashSet<>();
        Map<InetSocketAddress, Long> fileSources = sources.get(fileId);
        long now = getNow();
        Iterator<Map.Entry<InetSocketAddress, Long>> it = fileSources.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<InetSocketAddress, Long> source = it.next();
            if (now - source.getValue() > MAX_SEEDER_TIMEOUT) {
                it.remove();
            } else {
                seeders.add(source.getKey());
            }
        }
        return seeders;
    }

    private static long getNow() {
        return System.currentTimeMillis();
    }
}
