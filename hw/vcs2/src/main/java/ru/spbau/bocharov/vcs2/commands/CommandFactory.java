package ru.spbau.bocharov.vcs2.commands;

import ru.spbau.bocharov.vcs2.repo.Branch;
import ru.spbau.bocharov.vcs2.repo.Repository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private static CommandFactory instance = new CommandFactory();

    public static CommandFactory getInstance() {
        return instance;
    }

    public Command createCommand(String name, Repository repo) throws IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!registry.containsKey(name)) {
            throw new IOException("unknown command: " + name);
        }
        return (Command) registry.get(name).getConstructor(Repository.class).newInstance(repo);
    }

    private Map<String, Class> registry = new HashMap<>();

    private CommandFactory() {
        registry.put("init", InitCommand.class);
        registry.put("commit", CommitCommand.class);
        registry.put("log", LogCommand.class);
        registry.put("branch", BranchCommand.class);
        registry.put("checkout", CheckoutCommand.class);
        registry.put("add", AddCommand.class);
        registry.put("rm", RemoveCommand.class);
        registry.put("reset", ResetCommand.class);
        registry.put("clean", CleanCommand.class);
        registry.put("status", StatusCommand.class);
        registry.put("merge", MergeCommand.class);
    }
}
