package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BranchCommand extends BaseCommand {

    public BranchCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        if (args.size() > 2) {
            throw new IOException(
                    "branch: incorrect number of argmuents:" +
                    "expected: 1 or 2, got " + args.size());
        }

        if (Objects.equals(args.get(0), "-d")) {
            repository.removeBranch(args.get(1));
        } else {
            repository.createBranch(args.get(0));
        }

        System.out.println("successfully");
    }
}
