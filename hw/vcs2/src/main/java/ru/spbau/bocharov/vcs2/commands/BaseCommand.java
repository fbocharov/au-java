package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Repository;

public abstract class BaseCommand implements Command {

    protected final Repository repository;

    public BaseCommand(Repository repo) {
        repository = repo;
    }
}
