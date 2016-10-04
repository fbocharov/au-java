package ru.spbau.bocharov.vcs2.storage;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.bocharov.vcs2.storage.impl.StorageImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StorageTest {

    @Test
    public void shouldCreateDirectory() throws IOException {
        Storage storage = createStorage();

        storage.createDirectory(SIMPLE_DIR1);

        assertTrue(Files.exists(getRelativePath(SIMPLE_DIR1)));
    }

    @Test
    public void shouldCreateDirectoryIfOneAlreadyExists() throws IOException {
        Storage storage = createStorage();

        storage.createDirectory(SIMPLE_SUBDIR2);
        assertTrue(Files.exists(getRelativePath(SIMPLE_SUBDIR2)));

        storage.createDirectory(SIMPLE_DIR2, true);

        assertTrue(Files.exists(getRelativePath(SIMPLE_DIR2)));
        assertTrue(getRelativePath(SIMPLE_DIR2).toFile().list().length == 0);
    }

    @Test
    public void shouldDeletePath() throws IOException {
        Path path = getRelativePath(SIMPLE_DIR1);
        path.toFile().mkdirs();
        assertTrue(Files.exists(path));

        Storage storage = createStorage();
        storage.delete(SIMPLE_DIR1);

        assertTrue(!Files.exists(path));
    }

    @Test
    public void shouldCheckFileExists() throws IOException {
        Storage storage = createStorage();
        Path path = getRelativePath(SIMPLE_DIR1);
        assertEquals(Files.exists(path), storage.exists(SIMPLE_DIR1));

        storage.createDirectory(SIMPLE_SUBDIR2, true);
        path = getRelativePath(SIMPLE_SUBDIR2);
        assertEquals(Files.exists(path), storage.exists(SIMPLE_SUBDIR2));

        path = getRelativePath(Paths.get("nonexistent"));
        assertEquals(Files.exists(path), storage.exists(Paths.get("nonexistent")));
    }

    @Test
    public void shouldListExistent() throws IOException {
        Storage storage = createStorage();

        Path file1 = getRelativePath(SIMPLE_FILE21);
        Path file2 = getRelativePath(SIMPLE_FILE22);

        storage.createDirectory(SIMPLE_SUBDIR2, true);
        Files.createFile(file1);
        Files.createFile(file2);

        assertEquals(
                Arrays.asList(SIMPLE_FILE22,SIMPLE_FILE21),
                storage.list(Paths.get(""), p -> false));
    }

    @Test
    public void shouldCreateFileOnWrite() throws IOException {
        Storage storage = createStorage();

        Path file = getRelativePath(SIMPLE_FILE21);

        assertTrue(!Files.exists(file));

        String output = "hello, world!";
        try (OutputStream stream = storage.openForWrite(SIMPLE_FILE21)) {
            stream.write(output.getBytes());
        }

        assertTrue(Files.exists(file));
    }

    @Test
    public void shouldWriteAndRead() throws IOException {
        Storage storage = createStorage();

        String output = "hello, world!";
        try (OutputStream stream = storage.openForWrite(getRelativePath(SIMPLE_FILE21))) {
            stream.write(output.getBytes());
        }

        byte[] input = new byte[output.length()];
        try (InputStream stream = storage.openForRead(getRelativePath(SIMPLE_FILE21))) {
            stream.read(input, 0, output.length());
        }

        assertEquals(output, new String(input));
    }

    @Test
    public void shouldCountChecksum() throws IOException {
        Storage storage = createStorage();

        String output = "hello, world!";
        Path file = getRelativePath(SIMPLE_FILE21);
        try (OutputStream stream = storage.openForWrite(file)) {
            stream.write(output.getBytes());
        }

        assertEquals(1486392595L, storage.checksum(file));
    }

    @Before
    public void clean() throws IOException {
        cleanup();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        File testPath = getTestPath().toFile();
        for (String path: testPath.list()) {
            FileUtils.forceDelete(new File(testPath, path));
        }
    }


    private static final Path SIMPLE_DIR1 = Paths.get("dir1");
    private static final Path SIMPLE_DIR2 = Paths.get("dir2");
    private static final Path SIMPLE_SUBDIR2 = Paths.get(SIMPLE_DIR2.toString(), "subdir1");
    private static final Path SIMPLE_FILE21 = Paths.get(SIMPLE_DIR2.toString(), "file21.txt");
    private static final Path SIMPLE_FILE22 = Paths.get(SIMPLE_SUBDIR2.toString(), "file22.txt");

    private static Storage createStorage() {
        return new StorageImpl(getTestPath());
    }

    private static Path getTestPath() {
        return Paths.get("src", "test", "resources", "storage-tests");
    }

    private static Path getRelativePath(Path subpath) {
        return Paths.get(getTestPath().toString(), subpath.toString());
    }
}