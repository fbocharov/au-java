package ru.spbau.bocharov.vcs2.repo;

import java.io.Serializable;

public interface Revision extends Serializable {

    String getId();
    String getMessage();
}
