package ru.spbau.bocharov.vcs2.repo.impl;

import ru.spbau.bocharov.vcs2.repo.Revision;

import java.util.Set;

class RevisionImpl implements Revision {

    private final String revisionId;
    private final String message;
    private final Set<String> changedFiles;
    private final Set<String> removedFiles;
    private final Snapshot snapshot;

    public RevisionImpl(String id, String msg, Set<String> changed, Set<String> removed, Snapshot snp) {
        revisionId = id;
        message = msg;
        changedFiles = changed;
        removedFiles = removed;
        snapshot = snp;
    }

    @Override
    public String toString() {
        return String.format("Revision: %s\nMessage: %s", revisionId, message);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RevisionImpl && revisionId.equals(((RevisionImpl) other).getId());
    }

    @Override
    public String getId() {
        return revisionId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Set<String> getChangedFiles() {
        return changedFiles;
    }

    public Set<String> getRemovedFiles() {
        return removedFiles;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }
}
