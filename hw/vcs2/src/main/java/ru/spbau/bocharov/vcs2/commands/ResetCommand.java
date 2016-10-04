package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ResetCommand extends BaseCommand {

    public ResetCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        if (args.size() != 1) {
            throw new IOException("wrong number of arguments for reset");
        }

        repository.reset(Paths.get(args.get(0)));
    }
}
