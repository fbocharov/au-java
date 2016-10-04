package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CleanCommand extends BaseCommand {

    public CleanCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        Repository.State state = repository.getState();

        for (Path path: state.getUntrackedFiles()) {
            repository.remove(path);
        }

        System.out.println("successfully");
    }
}
