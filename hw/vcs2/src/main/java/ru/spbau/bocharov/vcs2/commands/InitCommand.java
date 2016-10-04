package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class InitCommand extends BaseCommand {

    public InitCommand(Repository repo) {
        super(repo);
    }

    @Override
    public void execute(List<String> args) throws IOException {
        repository.init();
        System.out.println("init empty repository");
    }
}
