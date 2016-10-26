package ru.spbau.bocharov.ftp.client.commands;

import java.util.HashMap;
import java.util.Map;


public class CommandFactory {

    private static final CommandFactory INSTANCE = new CommandFactory();

    public static CommandFactory getInstance() {
        return INSTANCE;
    }

    public Command createCommand(String commandName) throws CommandFactoryException {
        if (!registry.containsKey(commandName)) {
            throw new UnknownCommandException(String.format("unknown command : %s", commandName));
        }

        String error;
        try {
            return (Command) registry.get(commandName).newInstance();
        } catch (InstantiationException e) {
            error = String.format("%s cannot be instantiated: %s", commandName, e.getMessage());
        } catch (IllegalAccessException e) {
            error = String.format("%s doesn't have public constructor without parameters", commandName);
        }
        throw new BadCommandException(error);
    }

    private Map<String, Class> registry = new HashMap<>();
    private CommandFactory() {
        registry.put("list", ListCommand.class);
        registry.put("get",  GetCommand.class);
    }
}
