package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Branch;
import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.util.List;

public class CheckoutCommand extends BaseCommand {

    public CheckoutCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        if (args.size() != 2) {
            throw new IOException(
                    "checkout: incorrect number of arguments:" +
                    "expected: 2, got: " + args.size());
        }

        String branchOrRevision = args.get(1);
        Branch branch;
        switch (args.get(0)) {
            case "-r":
                branch = repository.unrollToRevision(branchOrRevision);
                break;
            case "-b":
                branch = repository.setCurrentBranch(branchOrRevision);
                break;
            default:
                throw new IOException(
                        "checkout: incorrect argument:" +
                        "expected: -r|-b, got: " + args.get(0));
        }

        System.out.println("switched to branch " + branch.getName());
    }
}
