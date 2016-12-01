package ru.spbau.bocharov.torrent.client.state;

import lombok.Getter;

import java.io.Serializable;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TorrentFile implements Serializable {

    public static final int PART_SIZE = 10 * 1024 * 1024; // 10Mb

    @Getter
    private final int fileId;
    @Getter
    private final String fileName;
    @Getter
    private final long size;
    private final long lastPartSize;
    private final int partCount;
    private long downloadedSize = 0;

    private final BitSet loadedParts;

    public static TorrentFile createEmpty(int id, String name, long size) {
        return new TorrentFile(id, name, size, false);
    }

    public static TorrentFile createFull(int id, String name, long size) {
        return new TorrentFile(id, name, size, true);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TorrentFile)) {
            return false;
        }

        TorrentFile other = (TorrentFile) o;
        return fileId == other.fileId && Objects.equals(fileName, other.fileName)
                && size == other.size && loadedParts == other.loadedParts;
    }

    @Override
    public int hashCode() {
        return fileId;
    }

    public synchronized boolean isDownloaded() {
        return loadedParts.length() == partCount;
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

    public double getDownloadedPercent() {
        return (double) downloadedSize / size;
    }

    public synchronized long getPartSize(int part) {
        if (!loadedParts.get(part) || part >= loadedParts.size()) {
            return 0;
        }

        return partCount > part + 1 ? PART_SIZE : lastPartSize;
    }

    synchronized void addPart(int part) {
        loadedParts.set(part);
        downloadedSize += getPartSize(part);
    }

    private TorrentFile(int id, String name, long sz, boolean full) {
        fileId = id;
        fileName = name;
        size = sz;
        partCount = (int) ((sz + PART_SIZE - 1) / PART_SIZE);
        loadedParts = new BitSet(partCount);
        loadedParts.set(0, partCount, full);
        lastPartSize = partCount > 0 ? size - (partCount - 1) * PART_SIZE : 0;
    }
}
