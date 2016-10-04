package ru.spbau.bocharov.vcs2.repo;

import java.io.Serializable;

public interface Branch extends Iterable<Revision>, Serializable {

    String getName();

    Revision getLatestRevision();
}
