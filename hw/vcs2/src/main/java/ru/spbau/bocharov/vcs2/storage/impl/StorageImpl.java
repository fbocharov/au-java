package ru.spbau.bocharov.vcs2.storage.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import ru.spbau.bocharov.vcs2.storage.Storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StorageImpl implements Storage{

    private final Path root;

    public StorageImpl(Path r) {
        root = r;
    }

    @Override
    public void createDirectory(Path path) throws IOException {
        createDirectory(path, false);
    }

    @Override
    public void createDirectory(Path path, boolean deleteExisting) throws IOException {
        if (deleteExisting) {
            delete(path);
        }
        getAbsolutePath(path).toFile().mkdirs();
    }

    @Override
    public void delete(Path path) throws IOException {
        path = getAbsolutePath(path);
        if (Files.exists(path)) {
            FileUtils.forceDelete(path.toFile());
        }
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(getAbsolutePath(path));
    }

    @Override
    public List<Path> list(Path path, Predicate<Path> filter) throws IOException {
        FileFilter ff = new FileFilter(filter);
        return FileUtils.listFiles(getAbsolutePath(path).toFile(), ff, ff).stream()
                .map(File::toPath).collect(Collectors.toList());
    }

    @Override
    public long checksum(Path path) throws IOException {
        return FileUtils.checksumCRC32(getAbsolutePath(path).toFile());
    }

    @Override
    public InputStream openForRead(Path path) throws IOException {
        return new FileInputStream(getAbsolutePath(path).toString());
    }

    @Override
    public OutputStream openForWrite(Path path) throws IOException {
        path = getAbsolutePath(path);
        if (!Files.exists(path)) {
            createDirectory(path.getParent());
            Files.createFile(path);
        }
        return new FileOutputStream(getAbsolutePath(path).toString());
    }


    private static final class FileFilter implements IOFileFilter {

        private final Predicate<Path> filter;

        private FileFilter(Predicate<Path> f) {
            filter = f;
        }

        @Override
        public boolean accept(File file) {
            return !filter.test(file.toPath());
        }

        @Override
        public boolean accept(File file, String s) {
            return !filter.test(file.toPath());
        }
    }

    private Path getAbsolutePath(Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return Paths.get(root.toString(), path.toString()).toAbsolutePath();
    }
}
