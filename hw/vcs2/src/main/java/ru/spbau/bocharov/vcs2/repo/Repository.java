package ru.spbau.bocharov.vcs2.repo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Repository {

    interface State {
        List<Path> getUntrackedFiles();
        List<Path> getChangedFiles();
        List<Path> getRemovedFiles();
    }

    void init() throws IOException;
    boolean initialized() throws IOException;

    void add(Path path) throws IOException;
    void remove(Path path) throws IOException;
    void reset(Path path) throws IOException;

    State getState() throws IOException;
    Revision commit(String message) throws IOException;

    Branch unrollToRevision(String revisionId) throws IOException;

    Branch getCurrentBranch() throws IOException;
    Branch setCurrentBranch(String name) throws IOException;
    void createBranch(String name) throws IOException;
    void removeBranch(String name) throws IOException;
    void mergeBranches(String into, String from) throws IOException;
}
