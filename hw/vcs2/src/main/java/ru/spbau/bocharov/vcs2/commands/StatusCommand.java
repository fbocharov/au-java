package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class StatusCommand extends BaseCommand {

    public StatusCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        Repository.State state = repository.getState();

        // TODO: add colored print
        print("Chanded files:", state.getChangedFiles());
        print("Removed files:", state.getRemovedFiles());
        print("Untracked files:", state.getUntrackedFiles());
    }

    private static void print(String top, List<Path> paths) {
        if (paths.isEmpty()) {
            return;
        }

        System.out.println(top);
        for (Path path: paths) {
            System.out.println("\t" + path.toString());
        }
        System.out.println();
    }
}
