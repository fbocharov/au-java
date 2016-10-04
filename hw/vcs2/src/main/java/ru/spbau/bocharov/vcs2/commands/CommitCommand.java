package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Branch;
import ru.spbau.bocharov.vcs2.repo.Repository;
import ru.spbau.bocharov.vcs2.repo.Revision;

import java.io.*;
import java.util.List;


public class CommitCommand extends BaseCommand {

    public CommitCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        if (!repository.initialized()) {
            throw new IOException("vcs2 repository not initialized!");
        }

        if (args.size() != 1) {
            throw new IOException("wrong number of arguments for commit");
        }

        try {
            Repository.State state = repository.getState();
            boolean stateChanged = !state.getChangedFiles().isEmpty() ||
                                   !state.getRemovedFiles().isEmpty();

            if (stateChanged) {
                Revision revision = repository.commit(args.get(0));
                System.out.println("commited at revision " + revision.getId());
            } else {
                System.out.println("Nothing to commit");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
