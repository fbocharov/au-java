package ru.spbau.bocharov.torrent.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    private static final long BUFFER_SIZE = 1024;

    public static void copy(InputStream in, OutputStream out, long size) throws IOException {
        byte[] buffer = new byte[1024];
        while (size > 0) {
            int readCount = in.read(buffer, 0, (int) Math.min(BUFFER_SIZE, size));
            out.write(buffer, 0, readCount);
            size -= readCount;
        }
    }

}
