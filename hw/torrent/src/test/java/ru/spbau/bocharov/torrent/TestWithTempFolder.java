package ru.spbau.bocharov.torrent;

import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.UncheckedIOException;

public class TestWithTempFolder {

    @ClassRule
    public static final TemporaryFolder FOLDER = new TemporaryFolder();

    protected static String createFolder(String name) {
        try {
            return FOLDER.newFolder(name).getPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected static String createFile(String file) {
        try {
            return FOLDER.newFile(file).getPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
