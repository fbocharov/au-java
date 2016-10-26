package ru.spbau.bocharov.ftp.client.commands;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class BaseCommandTest {

    @Test(expected= IOException.class)
    public void shouldThrowWhenNoArguments() throws Exception {
        createCommand().execute(null);
    }

    protected abstract Command createCommand() throws CommandFactoryException;
}
