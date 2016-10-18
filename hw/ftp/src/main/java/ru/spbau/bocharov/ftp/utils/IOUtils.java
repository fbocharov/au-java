package ru.spbau.bocharov.ftp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    private static final int BUFFER_SIZE = 1024;

    public static void copy(InputStream from, OutputStream to, long size) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (size > 0) {
            int readCount = from.read(buffer, 0, (int) Math.min(size, BUFFER_SIZE));
            to.write(buffer, 0, readCount);
            size -= readCount;
        }
    }
}
