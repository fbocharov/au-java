package ru.spbau.bocharov.vcs2.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    private static final int BUFFER_SIZE = 4096;

    public static void copy(InputStream in, OutputStream out, long size) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (size > 0) {
            int bytesRead = in.read(buffer, 0, (int) Math.min(BUFFER_SIZE, size));
            if (out != null) {
                out.write(buffer, 0, bytesRead);
            }
            size -= bytesRead;
        }
    }

}
