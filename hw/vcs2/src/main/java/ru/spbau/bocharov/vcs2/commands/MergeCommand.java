package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.util.List;

public class MergeCommand extends BaseCommand {

    public MergeCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        if (args.size() != 2) {
            throw new IOException("wrong number of arguments for merge");
        }

        repository.mergeBranches(args.get(0), args.get(1));
    }
}
