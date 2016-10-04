package ru.spbau.bocharov.vcs2.repo.impl;

import ru.spbau.bocharov.vcs2.repo.Branch;
import ru.spbau.bocharov.vcs2.repo.Revision;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class BranchImpl implements Branch {

    private final String name;
    private final List<RevisionImpl> revisions = new LinkedList<>();

    BranchImpl(String n) {
        name = n;
    }

    @Override
    public Revision getLatestRevision() {
        if (revisions.isEmpty()) {
            return null;
        }
        return revisions.get(revisions.size() - 1);
    }

    @Override
    public Iterator<Revision> iterator() {
        return new Iterator<Revision>() {
            private Iterator<RevisionImpl> slave = revisions.iterator();

            @Override
            public boolean hasNext() {
                return slave.hasNext();
            }

            @Override
            public Revision next() {
                return slave.next();
            }
        };
    }

    @Override
    public String getName() {
        return name;
    }

    List<RevisionImpl> getRevisions() {
        return revisions;
    }

    void addRevision(RevisionImpl revision) {
        revisions.add(revision);
    }

}
