package ru.spbau.bocharov.vcs2;

import ru.spbau.bocharov.vcs2.commands.Command;
import ru.spbau.bocharov.vcs2.commands.CommandFactory;
import ru.spbau.bocharov.vcs2.repo.Repository;
import ru.spbau.bocharov.vcs2.repo.impl.RepositoryImpl;
import ru.spbau.bocharov.vcs2.storage.Storage;
import ru.spbau.bocharov.vcs2.storage.impl.StorageImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class VCS2 {
    public static void main(String... args) throws Exception {
        Storage storage = new StorageImpl(Paths.get("").toAbsolutePath());
        Repository repository = new RepositoryImpl(storage);
        try {
            Command cmd = CommandFactory.getInstance().createCommand(args[0], repository);
            cmd.execute(Arrays.asList(args).subList(1, args.length));
        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
        }
    }
}
