package ru.spbau.bocharov.vcs2.repo.impl;

import ru.spbau.bocharov.vcs2.storage.Storage;
import ru.spbau.bocharov.vcs2.repo.Branch;
import ru.spbau.bocharov.vcs2.repo.Repository;
import ru.spbau.bocharov.vcs2.repo.Revision;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RepositoryImpl implements Repository {

    private final Storage storage;
    private final Predicate<Path> VCS_FILE_FILTER;


    public RepositoryImpl(Storage s) {
        storage = s;
        VCS_FILE_FILTER = p -> p.toAbsolutePath().startsWith(storage.getAbsolutePath(Layout.getVCSRootPath()));
    }

    @Override
    public void init() throws IOException {
        storage.createDirectory(Layout.getVCSRootPath(), true);
        storage.createDirectory(Layout.getBranchesPath());
        storage.createDirectory(Layout.getRevisionsPath());

        createEmptyBranch(Layout.MASTER_BRANCH);
        MetaInfo metaInfo = new MetaInfo();
        save(metaInfo, Layout.getRepoMetaInfoPath());
    }

    @Override
    public boolean initialized() throws IOException {
        return storage.exists(Layout.getVCSRootPath());
    }

    @Override
    public void add(Path path) throws IOException {
        if (!storage.exists(path)) {
            throw new IOException("path doesn't exist: " + path.toString());
        }

        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        metaInfo.fileChecksums.put(path.toString(), null);
        save(metaInfo, Layout.getRepoMetaInfoPath());
    }

    @Override
    public void remove(Path path) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());

        String p = path.toString();
        if (metaInfo.fileChecksums.containsKey(p)) {
            metaInfo.fileChecksums.remove(p);
            metaInfo.removedFiles.add(p);
            save(metaInfo, Layout.getRepoMetaInfoPath());
        }

        storage.delete(path);
    }

    @Override
    public void reset(Path path) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        if (!metaInfo.fileChecksums.containsKey(path.toString())) {
            throw new IOException("path " + path.toString() + " not under version control");
        }

        RevisionImpl latestWhichContains = null;
        for (RevisionImpl revision: getBranch(metaInfo.currentBranch).getRevisions()) {
            if (revision.getChangedFiles().contains(path.toString())) {
                latestWhichContains = revision;
            }
        }

        assert latestWhichContains != null;
        latestWhichContains.getSnapshot().restoreOne(path, storage);
    }


    @Override
    public State getState() throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        List<Path> untracked = new LinkedList<>();
        List<Path> changed = new LinkedList<>();
        List<Path> removed = metaInfo.removedFiles.stream().map(Paths::get).collect(Collectors.toList());

        storage.list(Layout.getProjectRoot(), VCS_FILE_FILTER).forEach(p -> {
            String path = p.toString();
            if (metaInfo.fileChecksums.containsKey(path)) {
                Long checksum = metaInfo.fileChecksums.get(path);
                try {
                    if (checksum == null || storage.checksum(p) != checksum) {
                        changed.add(p);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                untracked.add(p);
            }
        });

        return new StateImpl(untracked, changed, removed);
    }

    @Override
    public Revision commit(String message) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());

        Set<String> changedFiles = getChangedFiles(metaInfo);
        RevisionImpl revision = createRevision(
                Integer.toString(metaInfo.nextRevisionId++),
                changedFiles,
                metaInfo.removedFiles,
                message);
        BranchImpl branch = getBranch(metaInfo.currentBranch);
        branch.addRevision(revision);
        save(branch, Layout.getBranchPath(metaInfo.currentBranch));

        // update metainfo
        for (String file: changedFiles) {
            metaInfo.fileChecksums.put(file, storage.checksum(Paths.get(file)));
        }
        metaInfo.removedFiles.clear();
        save(metaInfo, Layout.getRepoMetaInfoPath());

        return revision;
    }

    @Override
    public Branch unrollToRevision(String revisionId) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        if (hasUncommitedChanges(metaInfo)) {
            throw new IOException("can't unroll to revision " + revisionId + ": there are uncommited changes");
        }

        boolean hasRevision = false;
        final List<RevisionImpl> revisions = new LinkedList<>();
        BranchImpl currentBranch = getBranch(metaInfo.currentBranch);
        for (RevisionImpl revision: currentBranch.getRevisions()) {
            revisions.add(revision);
            if (revision.getId().equals(revisionId)) {
                hasRevision = true;
                break;
            }
        }

        if (!hasRevision) {
            throw new IOException("current branch doesn't contain revision " + revisionId);
        }

        String branchName = "branch" + revisionId;
        BranchImpl branch = createEmptyBranch(branchName);
        for (RevisionImpl revision: revisions) {
            branch.addRevision(revision);
        }
        save(branch, Layout.getBranchPath(branchName));

        setCurrentBranch(branchName);

        return branch;
    }


    @Override
    public Branch getCurrentBranch() throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        return getBranch(metaInfo.currentBranch);
    }

    @Override
    public Branch setCurrentBranch(String name) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        if (hasUncommitedChanges(metaInfo)) {
            throw new IOException("can't switch to another branch: there are uncommited changes");
        }

        Path branchPath = Layout.getBranchPath(name);
        if (!storage.exists(branchPath)) {
            throw new IOException("branch " + name + " doesn't exist");
        }

        clearProject(metaInfo);
        metaInfo.currentBranch = name;
        BranchImpl branch = getBranch(name);
        for (RevisionImpl revision: branch.getRevisions()) {
            rollRevision(metaInfo, revision);
        }

        save(metaInfo, Layout.getRepoMetaInfoPath());

        return branch;
    }

    @Override
    public void createBranch(String name) throws IOException {
        Path branchPath = Layout.getBranchPath(name);
        if (storage.exists(branchPath)) {
            throw new IOException("branch " + name + " already exists");
        }

        BranchImpl branch = createEmptyBranch(name);
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        for (RevisionImpl revision: getBranch(metaInfo.currentBranch).getRevisions()) {
            branch.addRevision(revision);
        }
        save(branch, branchPath);
    }

    @Override
    public void removeBranch(String name) throws IOException {
        if (Objects.equals(name, Layout.MASTER_BRANCH)) {
            throw new IOException("you can't remove master");
        }

        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        if (Objects.equals(metaInfo.currentBranch, name)) {
            setCurrentBranch(Layout.MASTER_BRANCH);
        }

        storage.delete(Layout.getBranchPath(name));
    }

    @Override
    public void mergeBranches(String into, String from) throws IOException {
        MetaInfo metaInfo = load(Layout.getRepoMetaInfoPath());
        if (!metaInfo.currentBranch.equals(into)) {
            throw new IOException("can merge only into current branch");
        }

        BranchImpl intoBranch = getBranch(into);
        BranchImpl fromBranch = getBranch(from);

        clearProject(metaInfo);

        Iterator<RevisionImpl> intoIter = intoBranch.getRevisions().iterator();
        Iterator<RevisionImpl> fromIter = fromBranch.getRevisions().iterator();
        RevisionImpl intoRev = null;
        RevisionImpl fromRev = null;
        while (intoIter.hasNext() && fromIter.hasNext()) {
            intoRev = intoIter.next();
            fromRev = fromIter.next();

            if (intoRev.equals(fromRev)) {
                rollRevision(metaInfo, intoRev);
            }
        }

        while (intoRev != null) {
            rollRevision(metaInfo, intoRev);
            intoRev = null;
            if (intoIter.hasNext()) {
                intoRev = intoIter.next();
            }
        }

        Set<String> changedFiles = new HashSet<>();
        Set<String> removedFiles = new HashSet<>();
        while (fromRev != null) {
            rollRevision(metaInfo, fromRev);
            changedFiles.addAll(fromRev.getChangedFiles());
            removedFiles.addAll(fromRev.getRemovedFiles());
            fromRev = null;
            if (fromIter.hasNext()) {
                fromRev = fromIter.next();
            }
        }

        RevisionImpl mergeRevision = createRevision(
                Integer.toString(metaInfo.nextRevisionId++),
                changedFiles,
                removedFiles,
                "merge " + from + " into " + into);

        intoBranch.addRevision(mergeRevision);

        save(intoBranch, Layout.getBranchPath(metaInfo.currentBranch));
    }


    private final class StateImpl implements State {

        private final List<Path> untrackedFiles;
        private final List<Path> changedFiles;
        private final List<Path> removedFiles;

        private StateImpl(List<Path> untracked, List<Path> changed, List<Path> removed) {
            untrackedFiles = untracked;
            changedFiles = changed;
            removedFiles = removed;
        }

        @Override
        public List<Path> getUntrackedFiles() {
            return untrackedFiles;
        }

        @Override
        public List<Path> getChangedFiles() {
            return changedFiles;
        }

        @Override
        public List<Path> getRemovedFiles() {
            return removedFiles;
        }
    }

    private static final class MetaInfo implements Serializable {
        private int nextRevisionId = 0;
        private String currentBranch = Layout.MASTER_BRANCH;
        private final Map<String, Long> fileChecksums = new HashMap<>();
        private final Set<String> removedFiles = new HashSet<>();
    }

    private boolean hasUncommitedChanges(MetaInfo metaInfo) throws IOException {
        return !getChangedFiles(metaInfo).isEmpty() || !metaInfo.removedFiles.isEmpty() ;
    }

    private void clearProject(MetaInfo metaInfo) throws IOException {
        storage.list(Layout.getProjectRoot(), VCS_FILE_FILTER).forEach(p -> {
            try {
                if (metaInfo.fileChecksums.containsKey(p.toString())) {
                    storage.delete(p);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        metaInfo.fileChecksums.clear();
        metaInfo.removedFiles.clear();
    }

    private void rollRevision(MetaInfo metaInfo, RevisionImpl revision) throws IOException {
        revision.getSnapshot().restoreAll(storage);
        for (String file: revision.getChangedFiles()) {
            metaInfo.fileChecksums.put(file, storage.checksum(Paths.get(file)));
        }
        for (String file: revision.getRemovedFiles()) {
            metaInfo.fileChecksums.remove(file);
        }
    }

    private RevisionImpl createRevision(
            String revisionId,
            Set<String> changedFiles,
            Set<String> removedFiles,
            String message)
            throws IOException {
        Snapshot snapshot = Snapshot.createSnapshot(changedFiles,
                                                    Layout.getSnapshotPath(revisionId),
                                                    storage);

        return new RevisionImpl(revisionId,
                                message,
                                changedFiles,
                                removedFiles,
                                snapshot);
    }

    private BranchImpl createEmptyBranch(String name) throws IOException {
        BranchImpl branch = new BranchImpl(name);
        save(branch, Layout.getBranchPath(name));
        return branch;
    }

    private BranchImpl getBranch(String name) throws IOException {
        return load(Layout.getBranchPath(name));
    }

    private Set<String> getChangedFiles(MetaInfo metaInfo) throws IOException {
        Set<String> changedFiles = new HashSet<>();
        for (String path: metaInfo.fileChecksums.keySet()) {
            if (!storage.exists(Paths.get(path))) {
                metaInfo.removedFiles.add(path);
                metaInfo.fileChecksums.remove(path);
            } else {
                Long checksum = metaInfo.fileChecksums.get(path);
                if (checksum == null || storage.checksum(Paths.get(path)) != checksum) {
                    changedFiles.add(path);
                }
            }
        }
        return changedFiles;
    }

    private <T> T load(Path path) throws IOException {
        try (InputStream stream = storage.openForRead(path)) {
            return (T) new ObjectInputStream(stream).readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("panic: " + e.getMessage());
        }
    }

    private <T> void save(T object, Path path) throws IOException {
        try (OutputStream stream = storage.openForWrite(path)) {
            new ObjectOutputStream(stream).writeObject(object);
        }
    }
}
