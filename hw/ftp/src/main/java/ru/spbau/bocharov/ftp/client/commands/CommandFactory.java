package ru.spbau.bocharov.ftp.client.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class CommandFactory {

    private static final CommandFactory instance = new CommandFactory();

    public static CommandFactory getInstance() {
        return instance;
    }

    public Command createCommand(String commandName) throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        return (Command) commandRegistry.get(commandName).newInstance();
    }

    private Map<String, Class> commandRegistry = new HashMap<>();
    {
        commandRegistry.put("list", ListCommand.class);
        commandRegistry.put("get",  GetCommand.class);
    }
}
