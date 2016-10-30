package ru.spbau.bocharov.torrent.client.state;

import lombok.Getter;

import java.io.Serializable;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class TorrentFile implements Serializable {

    public static final int PART_SIZE = 10 * 1024 * 1024; // 10Mb

    @Getter
    private final int fileId;
    @Getter
    private final String fileName;
    private final long size;
    private final long lastPartSize;

    private final BitSet loadedParts;

    public static TorrentFile createEmpty(int id, String name, long size) {
        return new TorrentFile(id, name, size, false);
    }

    public static TorrentFile createFull(int id, String name, long size) {
        return new TorrentFile(id, name, size, true);
    }

    public synchronized boolean isDownloaded() {
        return loadedParts.cardinality() == loadedParts.length();
    }

    public synchronized boolean hasPart(int part) {
        return loadedParts.get(part);
    }

    public List<Integer> getParts() {
        BitSet partsClone;
        synchronized (this) {
            partsClone = (BitSet) loadedParts.clone();
        }

        List<Integer> parts = new LinkedList<>();
        for (int i = partsClone.nextSetBit(0); i != -1; i = partsClone.nextSetBit(i + 1)) {
            parts.add(i);
        }

        return parts;
    }

    public synchronized long getPartSize(int part) {
        if (!loadedParts.get(part) || part >= loadedParts.size()) {
            return 0;
        }

        return loadedParts.size() > part + 1 ? lastPartSize : PART_SIZE;
    }

    synchronized void addPart(int part) {
        loadedParts.flip(part);
    }

    private TorrentFile(int id, String name, long sz, boolean full) {
        fileId = id;
        fileName = name;
        size = sz;
        int partCount = (int) ((sz + PART_SIZE - 1) / PART_SIZE);
        loadedParts = new BitSet(partCount);
        loadedParts.set(0, partCount, full);
        lastPartSize = partCount > 0 ? size - (partCount - 1) * PART_SIZE : 0;
    }
}
