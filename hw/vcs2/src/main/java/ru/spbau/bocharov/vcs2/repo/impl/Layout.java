package ru.spbau.bocharov.vcs2.repo.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

class Layout {

    private static final String ROOT      = "";
    private static final String VCS_ROOT  = ".vcs2";
    private static final String BRANCHES  = "branches";
    private static final String REVISIONS = "revisions";

    static final String MASTER_BRANCH     = "master";

    static Path getProjectRoot() {
        return Paths.get(ROOT);
    }

    static Path getVCSRootPath() {
        return Paths.get(VCS_ROOT);
    }

    static Path getRepoMetaInfoPath() {
        return Paths.get(VCS_ROOT, ".state");
    }

    static Path getBranchesPath() {
        return Paths.get(VCS_ROOT, BRANCHES);
    }

    static Path getRevisionsPath() {
        return Paths.get(VCS_ROOT, REVISIONS);
    }

    static Path getBranchPath(String branch) {
        return Paths.get(VCS_ROOT, BRANCHES, branch);
    }

    static Path getSnapshotPath(String revision) {
        return Paths.get(VCS_ROOT, REVISIONS, revision, ".snapshot");
    }
}
