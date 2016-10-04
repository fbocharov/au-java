package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;
import ru.spbau.bocharov.vcs2.repo.Revision;

import java.io.IOException;
import java.util.List;

public class LogCommand extends BaseCommand {

    public LogCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        for (Revision revision: repository.getCurrentBranch()) {
            System.out.println(revision.toString());
            System.out.println();
        }
    }
}
