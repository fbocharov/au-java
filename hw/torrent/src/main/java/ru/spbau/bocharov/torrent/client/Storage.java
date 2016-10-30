package ru.spbau.bocharov.torrent.client;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Storage {

    private static final long BUFFER_SIZE = 1024;

    private final String basePath;

    public Storage(String path) {
        basePath = path;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    boolean exists(String name) {
        return Files.exists(getPath(name));
    }

    long fileSize(String name) {
        return getPath(name).toFile().length();
    }

    void copyWithOffset(String inPath, OutputStream out, long offset, long size) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(getPath(inPath).toString(), "r")) {
            raf.seek(offset);
            copy(Channels.newInputStream(raf.getChannel()), out, size);
        }
    }

    void copyWithOffset(InputStream stream, String outPath, long offset, long size) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(getPath(outPath).toString(), "rw")) {
            raf.seek(offset);
            copy(stream, Channels.newOutputStream(raf.getChannel()), size);
        }
    }

    private void copy(InputStream in, OutputStream out, long size) throws IOException {
        byte[] buffer = new byte[1024];
        while (size > 0) {
            int readCount = in.read(buffer, 0, (int) Math.min(BUFFER_SIZE, size));
            out.write(buffer, 0, readCount);
            size -= readCount;
        }
    }

    private Path getPath(String name) {
        return Paths.get(basePath, name);
    }
}
