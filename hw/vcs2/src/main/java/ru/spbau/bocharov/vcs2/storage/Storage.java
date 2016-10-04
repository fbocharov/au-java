package ru.spbau.bocharov.vcs2.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public interface Storage {

    void createDirectory(Path path) throws IOException;

    void createDirectory(Path path, boolean deleteExisting) throws IOException;

    void delete(Path path) throws IOException;

    boolean exists(Path path);

    long fileSize(Path path);

    List<Path> list(Path path, Predicate<Path> filter) throws IOException;

    long checksum(Path path) throws IOException;

    InputStream openForRead(Path path) throws IOException;

    OutputStream openForWrite(Path path) throws IOException;
}
