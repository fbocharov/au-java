package ru.spbau.bocharov.vcs2.repo;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.bocharov.vcs2.repo.impl.RepositoryImpl;
import ru.spbau.bocharov.vcs2.storage.Storage;
import ru.spbau.bocharov.vcs2.storage.impl.StorageImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

public class RepositoryTest {

    private static final Storage storage = new StorageImpl(getTestPath());

    private static final Path MASTER_DIR1 = Paths.get("dir1");
    private static final Path MASTER_DIR2 = Paths.get("dir2");
    private static final Path MASTER_FILE1 = Paths.get("dir1", "file1.txt");
    private static final Path MASTER_FILE2 = Paths.get("dir2", "file1.txt");

    private static final Path BRANCH_DIR3 = Paths.get("dir3");
    private static final Path BRANCH_FILE3 = Paths.get("dir3", "file3.txt");


    @Before
    public void prepare() throws IOException {
        cleanup();
        storage.createDirectory(MASTER_DIR1);
        storage.createDirectory(MASTER_DIR2);
        storage.createDirectory(BRANCH_DIR3);

        Files.createFile(Paths.get(getTestPath().toString(), MASTER_FILE1.toString()));
        Files.createFile(Paths.get(getTestPath().toString(), MASTER_FILE2.toString()));
        Files.createFile(Paths.get(getTestPath().toString(), BRANCH_FILE3.toString()));
    }

    @Test
    public void shouldInitEmptyRepository() throws IOException {
        Repository repository = createRepository();

        assertFalse(repository.initialized());
        repository.init();
        assertTrue(repository.initialized());
    }

    @Test
    public void shouldListUntrackedFiles() throws IOException {
        Repository repository = createRepository();
        repository.init();

        Repository.State state = repository.getState();
        assertTrue(state.getChangedFiles().isEmpty());
        assertTrue(state.getRemovedFiles().isEmpty());
        assertEquals(
                Arrays.asList(MASTER_FILE2, MASTER_FILE1, BRANCH_FILE3),
                state.getUntrackedFiles());
    }

    @Test
    public void shouldAddAndCommitFiles() throws IOException {
        Repository repository = createRepository();
        repository.init();

        Repository.State state = repository.getState();
        assertTrue(state.getChangedFiles().isEmpty());

        repository.add(MASTER_FILE1);
        state = repository.getState();
        assertEquals(1, state.getChangedFiles().size());

        repository.commit("file1");
        state = repository.getState();
        assertTrue(state.getChangedFiles().isEmpty());
    }

    @Test
    public void shouldRemoveFile() throws IOException {
        Repository repository = createRepository();
        repository.init();

        repository.add(MASTER_FILE1);
        repository.commit("file1");

        repository.remove(MASTER_FILE1);
        Repository.State state = repository.getState();
        assertEquals(1, state.getRemovedFiles().size());

        repository.commit("remove");
        state = repository.getState();
        assertTrue(state.getRemovedFiles().isEmpty());
    }

    @Test
    public void shouldSwitchBetweenBranches() throws IOException {
        Repository repository = createRepository();
        repository.init();

        repository.add(MASTER_FILE1);
        repository.add(MASTER_FILE2);
        repository.commit("file1 file2");

        repository.createBranch("branch");
        repository.setCurrentBranch("branch");

        repository.remove(MASTER_FILE1);
        repository.remove(MASTER_FILE2);
        repository.add(BRANCH_FILE3);
        repository.commit("remove add");

        assertFalse(storage.exists(MASTER_FILE1));
        assertFalse(storage.exists(MASTER_FILE2));
        assertTrue(storage.exists(BRANCH_FILE3));

        repository.setCurrentBranch("master");
        assertTrue(storage.exists(MASTER_FILE1));
        assertTrue(storage.exists(MASTER_FILE2));
        assertFalse(storage.exists(BRANCH_FILE3));
    }

    @Test
    public void shouldResetFileState() throws IOException {
        Repository repository = createRepository();
        repository.init();

        repository.add(MASTER_FILE1);
        repository.commit("file1");
        assertEquals(0, storage.fileSize(MASTER_FILE1));

        storage.openForWrite(MASTER_FILE1).write("12345".getBytes());
        assertEquals(5, storage.fileSize(MASTER_FILE1));

        repository.reset(MASTER_FILE1);
        assertEquals(0, storage.fileSize(MASTER_FILE1));
    }

    @Test
    public void shouldMergeBranches() throws IOException {
        Repository repository = createRepository();
        repository.init();

        repository.createBranch("branch");

        repository.add(MASTER_FILE1);
        repository.add(MASTER_FILE2);
        repository.commit("file1 file2");

        repository.setCurrentBranch("branch");

        assertFalse(storage.exists(MASTER_FILE1));
        assertFalse(storage.exists(MASTER_FILE2));

        repository.add(BRANCH_FILE3);
        repository.commit("file3");

        repository.mergeBranches("branch", "master");
        assertTrue(storage.exists(MASTER_FILE1));
        assertTrue(storage.exists(MASTER_FILE2));
        assertTrue(storage.exists(BRANCH_FILE3));
    }

    @AfterClass
    public static void cleanup() throws IOException {
        File testPath = getTestPath().toFile();
        for (String path: testPath.list()) {
            FileUtils.forceDelete(new File(testPath, path));
        }
    }


    private static Repository createRepository() {
        return new RepositoryImpl(storage);
    }

    private static Path getTestPath() {
        return Paths.get("src", "test", "resources", "repository-tests");
    }
}